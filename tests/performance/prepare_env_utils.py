import time

import faker
import requests

from auth0.authentication import GetToken
from auth0.management import Auth0
from auth0.rest import RestClientOptions
from faker import Faker

fake = Faker()

domain = 'rasztabigab.eu.auth0.com'
password = 'qabdyq-kepTed-xikke2'

roles_mapping = {
    "DELIVERY_COURIER": 'rol_GDbIwQiUkSnyeCW7',
    "ORDERING_USER": 'rol_MvPzd8SjHodMWQU5',
    "RESTAURANT_MANAGER": 'rol_3opY7722YsNbFEwD'
}


def get_access_token(username):
    url = "https://rasztabigab.eu.auth0.com/oauth/token"

    headers = {
        'Content-Type': 'application/x-www-form-urlencoded'
    }

    response = requests.request("POST", url, headers=headers, data={
        "grant_type": "password",
        "audience": "https://thesis.rasztabiga.me/api",
        "username": username,
        "password": password,
        "client_id": "BVxjiTTdPPXMpUj8gm1KoGgkkQ1fObF4",
        "client_secret": "gqzR7iyZF5N-l9RTRP9CYCds57AhuHewOsQ3VdPyXxovMPvI61F3-DdXw_qNbNhu",
        "scope": "read:users write:users read:restaurants write:restaurants read:deliveries write:deliveries read:couriers write:couriers read:orders write:orders"
    })

    return response.json().get("access_token")


def get_auth0_token():
    non_interactive_client_id = 'wzcQGP1lbnOuzAqkWaIGH3gNTLfPNRwb'
    non_interactive_client_secret = 'dn_fDGeETLLPy4mVBqrj1K4Hg8wezkGNBbVz47Kqgs0Maapwep4HayBvQXwUTReM'

    get_token = GetToken(domain, non_interactive_client_id, client_secret=non_interactive_client_secret)
    mgmt_api_token = get_token.client_credentials('https://{}/api/v2/'.format(domain))['access_token']

    return mgmt_api_token


token = get_auth0_token()


def create_auth0():
    rest_options = RestClientOptions(telemetry=False, timeout=30, retries=10)
    auth0 = Auth0(domain, token, rest_options)
    return auth0


def get_all_auth0_users():
    auth0 = create_auth0()
    users = []
    page = 0
    while True:
        page_users = auth0.users.list(page=page, per_page=100)["users"]
        if len(page_users) == 0:
            break
        users.extend(page_users)
        page += 1
    return users


def delete_auth0_user(user_id):
    auth0 = create_auth0()
    auth0.users.delete(user_id)


def delete_all_tmp_auth0_users():
    users = get_all_auth0_users()
    ignored_emails = ["delivery@thesis.rasztabiga.me", "restaurant@thesis.rasztabiga.me", "user@thesis.rasztabiga.me"]
    for user in users:
        if user["email"] not in ignored_emails:
            user_id = user["user_id"]
            print("Deleting user with id", user_id)
            delete_auth0_user(user_id)
            time.sleep(1)


def create_auth0_user(role):
    auth0 = create_auth0()
    user = auth0.users.create({
        "connection": "Username-Password-Authentication",
        "email": fake.uuid4()[:5] + "-" + role + "@thesis.rasztabiga.me",
        "password": password,
        "email_verified": True
    })
    user_id = user["user_id"]

    role_id = roles_mapping[role]

    auth0.users.add_roles(user_id, [role_id])

    return user["email"]


if __name__ == "__main__":
    delete_all_tmp_auth0_users()
