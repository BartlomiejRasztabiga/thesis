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

DEBUG = True


def log(*args, **kwargs):
    if DEBUG:
        return print(*args, **kwargs)


class OrderingUser(HttpUser):
    host = "http://thesis.rasztabiga.me/api"
    weight = 10

    def on_start(self):
        self.client.headers["X-User-Id"] = fake.uuid4()

        self._create_user()
        self._create_delivery_address()

        # log(f"ORDERING Created user with id {self.user_id} and delivery address")

    @task
    def e2e(self):
        time.sleep(1)
        restaurants = self.client.get("/v2/restaurants").json()
        if len(restaurants) == 0:
            return

        selected_restaurant = random.choice(restaurants)

        # TODO how to ensure, that we've selected proper restaurant?

        # TODO fails with User with ID userId not found?

        time.sleep(1)
        order_id = None
        while True:
            try:
                order_id = self.client.post("/v1/orders", json={
                    "restaurantId": selected_restaurant.get("id")
                }).json().get("id")

                log(f"ORDERING Created order with id {order_id}")

                if order_id is not None:
                    break
                log("ORDERING order_id is None")
            except Exception as e:
                log(f"ORDERING exception occurred {e}")
                continue

        while True:
            number_of_products = random.randint(1, 5)

            selected_restaurant = self.client.get(f"/v2/restaurants/{selected_restaurant.get('id')}").json()
            menu = selected_restaurant.get("menu")

            log(f"ORDERING number_of_products {number_of_products}")

            if len(menu) == 0:
                log("ORDERING menu is empty")
                time.sleep(1)
                continue
            else:
                break

        for _ in range(number_of_products):
            selected_product = random.choice(menu)
            log(f"ORDERING selected_product {selected_product}")
            self.client.post(f"/v1/orders/{order_id}/items", json={
                "productId": selected_product.get("id")
            })
            log(f"ORDERING added product {selected_product} to order {order_id}")

        log(f"ORDERING Finalizing order {order_id}")
        self.client.put(f"/v1/orders/{order_id}/finalize")

        # TODO wait for paymentId to be set
        payment_id = None
        while True:
            try:
                time.sleep(1)
                order = self.client.get(f"/v2/orders/{order_id}").json()
                payment_id = order.get("paymentId")
                if payment_id is not None:
                    log(f"ORDERING payment_id {payment_id}")
                    break
                else:
                    log(f"ORDERING payment_id is None")
            except Exception as e:
                log(f"ORDERING exception occurred {e}")
                continue

        log(f"ORDERING Paying for order {order_id}")
        self.client.put(f"/v1/payments/{payment_id}/pay")
        log(f"ORDERING Paid for order {order_id}")

        # TODO wait for PAID status

        while True:
            try:
                time.sleep(1)
                order = self.client.get(f"/v2/orders/{order_id}").json()
                # log(f"ORDERING Order status: {order.get('status')}")
                if order.get("status") == "DELIVERED":
                    # log(f"ORDERING Order delivered")
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
    weight = 1

    def on_start(self):
        self.client.headers["X-User-Id"] = fake.uuid4()
        self._create_restaurant()

        # log(f"RESTAURANT Created restaurant with id {self.restaurant_id}")

    @task
    def e2e(self):
        time.sleep(1)
        restaurant_orders = self.client.get(f"/v2/restaurants/{self.restaurant_id}/orders").json()
        # log(f"RESTAURANT Found {restaurant_orders} orders")
        restaurant_orders = list(filter(lambda order: order.get("status") == "NEW", restaurant_orders))
        # log(f"RESTAURANT Found {restaurant_orders} new orders")
        if len(restaurant_orders) == 0:
            return

        selected_order = random.choice(restaurant_orders)

        self.client.put(f"/v1/restaurants/{self.restaurant_id}/orders/{selected_order.get('restaurantOrderId')}/accept")
        log(f"RESTAURANT Accepted order")

        self.client.put(
            f"/v1/restaurants/{self.restaurant_id}/orders/{selected_order.get('restaurantOrderId')}/prepare")
        log(f"RESTAURANT Prepared order")

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
    weight = 3

    def on_start(self):
        self.client.headers["X-User-Id"] = fake.uuid4()

        self._create_courier()

        # log(f"DELIVERY Created courier with id {self.courier_id}")

    @task
    def e2e(self):
        # TODO does it work?
        while True:
            with self.client.put(f"/v1/deliveries/offer", catch_response=True) as response:
                if response.status_code == 404:
                    log(f"DELIVERY No offers")
                    response.success()
                    time.sleep(1)
                    continue
                else:
                    log(f"DELIVERY Assigned offer? {response.status_code}")
                    if response.status_code != 200:
                        log(f"DELIVERY error assigning offer ${response.json()}")
                        break
                    response.success()
                    break

        time.sleep(5)
        offer = None
        retries = 0
        while True:
            try:
                offer = self.client.get(f"/v2/deliveries/current").json()
                retries += 1
                if retries > 10:
                    log(f"DELIVERY retries exceeded")
                    return
                log(f"DELIVERY offer {offer}")
                if offer.get('id') is not None:
                    log(f"DELIVERY Found offer {offer}")
                    break
                else:
                    log(f"DELIVERY offer is None")
            except Exception as e:
                log(f"DELIVERY exception occurred {e}")
                continue

        # TODO delete rejecting?
        # if random.random() < 0.1:
        #     self.client.put(f"/v1/deliveries/{offer.get('id')}/reject")
        #     log(f"DELIVERY Rejected offer {offer}")
        #     return

        self.client.put(f"/v1/deliveries/{offer.get('id')}/accept")
        log(f"DELIVERY Accepted offer {offer}")

        # TODO wait for order to be ready for pickup?
        while True:
            try:
                self.client.put(f"/v1/deliveries/{offer.get('id')}/pickup")
                log(f"DELIVERY Picked up order {offer}")
                break
            except:
                continue

        self.client.put(f"/v1/deliveries/{offer.get('id')}/deliver")
        log(f"DELIVERY Delivered order {offer}")

    def _create_courier(self):
        self.courier_id = self.client.post("/v1/couriers", json={
            "name": fake.name(),
            "email": "contact@rasztabiga.me"
        }).json().get("id")

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


if __name__ == "__main__":
    run_single_user(DeliveryCourier)
