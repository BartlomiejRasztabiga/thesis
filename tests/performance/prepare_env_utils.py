import requests


def get_ordering_user_access_token():
    url = "https://rasztabigab.eu.auth0.com/oauth/token"

    headers = {
        'Content-Type': 'application/x-www-form-urlencoded'
    }

    response = requests.request("POST", url, headers=headers, data={
        "grant_type": "password",
        "audience": "https://thesis.rasztabiga.me/api",
        "username": "user@thesis.rasztabiga.me",
        "password": "qabdyq-kepTed-xikke2",
        "client_id": "BVxjiTTdPPXMpUj8gm1KoGgkkQ1fObF4",
        "client_secret": "gqzR7iyZF5N-l9RTRP9CYCds57AhuHewOsQ3VdPyXxovMPvI61F3-DdXw_qNbNhu",
        "scope": "read:users write:users read:orders write:orders read:restaurants"
    })

    return response.json().get("access_token")


def get_restaurant_manager_access_token():
    url = "https://rasztabigab.eu.auth0.com/oauth/token"

    headers = {
        'Content-Type': 'application/x-www-form-urlencoded'
    }

    response = requests.request("POST", url, headers=headers, data={
        "grant_type": "password",
        "audience": "https://thesis.rasztabiga.me/api",
        "username": "restaurant@thesis.rasztabiga.me",
        "password": "sazni3-docmaj-foNnuz",
        "client_id": "BVxjiTTdPPXMpUj8gm1KoGgkkQ1fObF4",
        "client_secret": "gqzR7iyZF5N-l9RTRP9CYCds57AhuHewOsQ3VdPyXxovMPvI61F3-DdXw_qNbNhu",
        "scope": "read:restaurants write:restaurants"
    })

    return response.json().get("access_token")


def get_delivery_courier_access_token():
    url = "https://rasztabigab.eu.auth0.com/oauth/token"

    headers = {
        'Content-Type': 'application/x-www-form-urlencoded'
    }

    response = requests.request("POST", url, headers=headers, data={
        "grant_type": "password",
        "audience": "https://thesis.rasztabiga.me/api",
        "username": "delivery@thesis.rasztabiga.me",
        "password": "Fuzpa9-sygfyx-xurjif",
        "client_id": "BVxjiTTdPPXMpUj8gm1KoGgkkQ1fObF4",
        "client_secret": "gqzR7iyZF5N-l9RTRP9CYCds57AhuHewOsQ3VdPyXxovMPvI61F3-DdXw_qNbNhu",
        "scope": "read:deliveries write:deliveries read:couriers write:couriers"
    })

    return response.json().get("access_token")
