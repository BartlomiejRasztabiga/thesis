import random
import time

import locust.exception
from locust import HttpUser, task, events, run_single_user
from faker import Faker

fake = Faker()

addresses = [
    {"streetAddress": "Bukowińska 26C, 02-703 Warszawa", "lat": "52.1840", "lng": "21.0251"},
    {"streetAddress": "Cypryjska 70, 02-762 Warszawa", "lat": "52.1730", "lng": "21.0585"},
    {"streetAddress": "Oskara Langego 8, 02-685 Warszawa", "lat": "52.1818", "lng": "21.0086"},
]


# TODO add more GETs to verify that the data is changed
# TODO add util to await for the data to be changed instead of using while True

class OrderingUser(HttpUser):
    host = "http://thesis.rasztabiga.me/api"

    def on_start(self):
        self.client.headers["X-User-Id"] = fake.uuid4()

        self._create_user()
        self._create_delivery_address()

        # print(f"ORDERING Created user with id {self.user_id} and delivery address")

    @task
    def e2e(self):
        time.sleep(1)
        restaurants = self.client.get("/v2/restaurants").json()
        if len(restaurants) == 0:
            return

        selected_restaurant = random.choice(restaurants)

        # TODO how to ensure, that we've selected proper restaurant?

        # TODO fails with User with ID userId not found?

        order_id = None
        while True:
            try:
                order_id = self.client.post("/v1/orders", json={
                    "restaurantId": selected_restaurant.get("id")
                }).json().get("id")

                if order_id is not None:
                    break
            except:
                continue

        number_of_products = random.randint(1, 5)
        menu = selected_restaurant.get("menu")

        if len(menu) == 0:
            return

        for _ in range(number_of_products):
            selected_product = random.choice(menu)
            self.client.post(f"/v1/orders/{order_id}/items", json={
                "productId": selected_product.get("id")
            })

        self.client.put(f"/v1/orders/{order_id}/finalize")

        # TODO wait for paymentId to be set
        payment_id = None
        while True:
            try:
                time.sleep(1)
                order = self.client.get(f"/v2/orders/{order_id}").json()
                payment_id = order.get("paymentId")
                if payment_id is not None:
                    break
            except:
                continue
        self.client.put(f"/v1/payments/{payment_id}/pay")

        # print(f"ORDERING Paid for order {order_id}")

        # TODO wait for PAID status

        while True:
            try:
                time.sleep(1)
                order = self.client.get(f"/v2/orders/{order_id}").json()
                # print(f"ORDERING Order status: {order.get('status')}")
                if order.get("status") == "DELIVERED":
                    # print(f"ORDERING Order delivered")
                    break
            except:
                continue

    def _create_user(self):
        self.user_id = self.client.post("/v1/users", json={
            "name": fake.name(),
            "email": "contact@rasztabiga.me"
        }).json().get("id")

    def _create_delivery_address(self):
        self.client.post(f"/v1/users/{self.user_id}/addresses", json={
            "address": random.choice(addresses).get("streetAddress"),
            "additionalInfo": None,
        })


class RestaurantManager(HttpUser):
    host = "http://thesis.rasztabiga.me/api"

    def on_start(self):
        self.client.headers["X-User-Id"] = fake.uuid4()
        self._create_restaurant()

        # print(f"RESTAURANT Created restaurant with id {self.restaurant_id}")

    @task
    def e2e(self):
        time.sleep(1)
        restaurant_orders = self.client.get(f"/v2/restaurants/{self.restaurant_id}/orders").json()
        # print(f"RESTAURANT Found {restaurant_orders} orders")
        restaurant_orders = list(filter(lambda order: order.get("status") == "NEW", restaurant_orders))
        # print(f"RESTAURANT Found {restaurant_orders} new orders")
        if len(restaurant_orders) == 0:
            return

        selected_order = random.choice(restaurant_orders)

        self.client.put(f"/v1/restaurants/{self.restaurant_id}/orders/{selected_order.get('restaurantOrderId')}/accept")
        # print(f"RESTAURANT Accepted order")

        self.client.put(
            f"/v1/restaurants/{self.restaurant_id}/orders/{selected_order.get('restaurantOrderId')}/prepare")
        # print(f"RESTAURANT Prepared order")

    def _create_restaurant(self):
        self.restaurant_id = self.client.post("/v1/restaurants", json={
            "name": fake.name(),
            "address": random.choice(addresses).get("streetAddress"),
            "email": "contact@rasztabiga.me",
            "imageUrl": "https://mui.com/static/images/cards/contemplative-reptile.jpg"

        }).json().get("id")

        self.client.put(f"/v1/restaurants/{self.restaurant_id}/menu", json={
            "menu": [
                {
                    "name": "Product #1",
                    "description": "Description",
                    "price": 21.37,
                    "imageUrl": "https://www.foodiesfeed.com/wp-content/uploads/2023/04/cheeseburger.jpg"
                },
                {
                    "name": "Product #2",
                    "description": "Description 2",
                    "price": 23.37,
                    "imageUrl": "https://www.foodiesfeed.com/wp-content/uploads/2023/04/cheeseburger.jpg"
                }
            ]
        })

        self.client.put(f"/v1/restaurants/{self.restaurant_id}/availability", json={
            "availability": "OPEN"
        })


class DeliveryCourier(HttpUser):
    host = "http://thesis.rasztabiga.me/api"

    def on_start(self):
        self.client.headers["X-User-Id"] = fake.uuid4()

        self._create_courier()

        # print(f"DELIVERY Created courier with id {self.courier_id}")

    @task
    def e2e(self):
        # TODO update courier location
        location = random.choice(addresses)
        self.client.put(f"/v1/couriers/me/location", json={
            "location": {
                "lat": location.get("lat"),
                "lng": location.get("lng")
            }
        })

        # TODO update courier availability
        self.client.put(f"/v1/couriers/me/availability", json={
            "availability": "ONLINE"
        })

        # TODO does it work?
        with self.client.put(f"/v1/deliveries/offer", catch_response=True) as response:
            if response.status_code == 404:
                response.success()
                return
            else:
                response.success()

        time.sleep(1)
        offer = None
        while True:
            try:
                offer = self.client.get(f"/v2/deliveries/current").json()
                if offer.get('id') is not None:
                    break
            except:
                continue

        # print(f"DELIVERY Found offer {offer}")

        # TODO delete rejecting?
        # if random.random() < 0.1:
        #     self.client.put(f"/v1/deliveries/{offer.get('id')}/reject")
        #     print(f"DELIVERY Rejected offer {offer}")
        #     return

        self.client.put(f"/v1/deliveries/{offer.get('id')}/accept")
        # print(f"DELIVERY Accepted offer {offer}")

        # TODO wait for order to be ready for pickup?
        while True:
            try:
                self.client.put(f"/v1/deliveries/{offer.get('id')}/pickup")
                # print(f"DELIVERY Picked up order {offer}")
                break
            except:
                continue

        self.client.put(f"/v1/deliveries/{offer.get('id')}/deliver")
        # print(f"DELIVERY Delivered order {offer}")

    def _create_courier(self):
        self.courier_id = self.client.post("/v1/couriers", json={
            "name": fake.name(),
            "email": "contact@rasztabiga.me"
        }).json().get("id")


if __name__ == "__main__":
    run_single_user(OrderingUser)
