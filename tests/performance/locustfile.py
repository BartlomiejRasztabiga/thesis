import random
import time

from locust import HttpUser, task
import requests
import json
from faker import Faker

import prepare_env_utils

fake = Faker()

addresses = [
    "Bukowińska 26C, 02-703 Warszawa",
    "Cypryjska 70, 02-762 Warszawa",
    "Oskara Langego 8, 02-685 Warszawa"
]


# TODO create users using auth0 API


class OrderingUser(HttpUser):
    def on_start(self):
        self.client.headers["Authorization"] = f"Bearer {prepare_env_utils.get_ordering_user_access_token()}"

        self._create_user()
        self._create_delivery_address()

        print(f"ORDERING Created user with id {self.user_id} and delivery address with id {self.delivery_address_id}")

    @task
    def e2e(self):
        restaurants = self.client.get("/restaurants").json()
        if len(restaurants) == 0:
            time.sleep(10)
            return

        selected_restaurant = random.choice(restaurants)

        order_id = self.client.post("/orders", json={
            "restaurantId": selected_restaurant.get("id")
        }).json().get("id")

        number_of_products = random.randint(1, 5)
        menu = selected_restaurant.get("menu")

        if len(menu) == 0:
            return

        for _ in range(number_of_products):
            selected_product = random.choice(menu)
            self.client.post(f"/orders/{order_id}/items", json={
                "productId": selected_product.get("id")
            })

        self.client.put(f"/orders/{order_id}/finalize", json={
            "deliveryAddressId": self.delivery_address_id
        })

        time.sleep(1)

        order = self.client.get(f"/orders/{order_id}").json()
        payment_id = order.get("paymentId")
        self.client.put(f"/payments/{payment_id}/pay")

        print(f"ORDERING Paid for order {order_id}")

        while True:
            order = self.client.get(f"/orders/{order_id}").json()
            print(f"ORDERING Order status: {order.get('status')}")
            if order.get("status") == "DELIVERED":
                print(f"ORDERING Order delivered")
                break
            time.sleep(10)  # refreshing every 10 seconds

    def _create_user(self):
        # TODO may already exist
        self.client.post("/users", json={
            "name": fake.name(),
            "email": "contact@rasztabiga.me"
        })

        self.user_id = self.client.get("/users/me").json().get("id")

    def _create_delivery_address(self):
        self.delivery_address_id = self.client.post(f"/users/{self.user_id}/addresses", json={
            "address": random.choice(addresses),
            "additionalInfo": "additional info",
        }).json().get("id")


class RestaurantManager(HttpUser):
    def on_start(self):
        self.client.headers["Authorization"] = f"Bearer {prepare_env_utils.get_restaurant_manager_access_token()}"

        self._create_restaurant()

        print(f"RESTAURANT Created restaurant with id {self.restaurant_id}")

    @task
    def e2e(self):
        restaurant_orders = self.client.get(f"/restaurants/{self.restaurant_id}/orders").json()
        restaurant_orders = list(filter(lambda order: order.get("status") == "NEW", restaurant_orders))
        if len(restaurant_orders) == 0:
            time.sleep(10)
            return

        selected_order = random.choice(restaurant_orders)

        self.client.put(f"/restaurants/{self.restaurant_id}/orders/{selected_order.get('restaurantOrderId')}/accept")
        print(f"RESTAURANT Accepted order")

        sleep_time = random.randint(60, 5 * 60)
        print(f"RESTAURANT Sleeping for {sleep_time} seconds")
        time.sleep(sleep_time)

        self.client.put(f"/restaurants/{self.restaurant_id}/orders/{selected_order.get('restaurantOrderId')}/prepare")
        print(f"RESTAURANT Prepared order")

    def _create_restaurant(self):
        self.restaurant_id = self.client.post("/restaurants", json={
            "id": fake.uuid4(),
            "name": fake.name(),
            "email": "contact@rasztabiga.me",
            "address": random.choice(addresses)
        }).json().get("id")

        self.client.put(f"/restaurants/{self.restaurant_id}/availability", json={
            "availability": "OPEN"
        })

        self.client.put(f"/restaurants/{self.restaurant_id}/menu", json={
            "menu": [
                {
                    "name": "Product #1",
                    "description": "Description",
                    "price": 21.37
                },
                {
                    "name": "Product #2",
                    "description": "Description 2",
                    "price": 23.37
                }
            ]
        })


class DeliveryCourier(HttpUser):
    def on_start(self):
        self.client.headers["Authorization"] = f"Bearer {prepare_env_utils.get_delivery_courier_access_token()}"

        self._create_courier()

        print(f"DELIVERY Created courier with id {self.courier_id}")

    @task
    def e2e(self):
        with self.client.get(f"/deliveries/offer?courierAddress={self.courier_address}", catch_response=True) as response:
            if response.status_code == 404:
                response.success()
                return
            else:
                offer = response.json()
                response.success()

        print(f"DELIVERY Found offer {offer}")

        if random.random() < 0.5:
            self.client.put(f"/deliveries/{offer.get('id')}/reject")
            print(f"DELIVERY Rejected offer {offer}")
            return

        self.client.put(f"/deliveries/{offer.get('id')}/accept")
        print(f"DELIVERY Accepted offer {offer}")

        sleep_time = random.randint(60, 5 * 60)
        print(f"DELIVERY Sleeping for {sleep_time} seconds")
        time.sleep(sleep_time)

        while True:
            try:
                self.client.put(f"/deliveries/{offer.get('id')}/pickup")
                print(f"DELIVERY Picked up order {offer}")
                break
            except:
                time.sleep(30)
                continue

        sleep_time = random.randint(60, 5 * 60)
        print(f"DELIVERY Sleeping for {sleep_time} seconds")
        time.sleep(sleep_time)

        self.client.put(f"/deliveries/{offer.get('id')}/deliver")
        print(f"DELIVERY Delivered order {offer}")

    def _create_courier(self):
        # TODO may already exist
        self.client.post("/couriers", json={
            "name": fake.name(),
            "email": "contact@rasztabiga.me"
        }).json().get("id")

        self.courier_id = self.client.get("/couriers/me").json().get("id")

        self.courier_address = random.choice(addresses)
