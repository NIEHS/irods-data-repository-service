import json

import requests
import logging

from requests.auth import HTTPBasicAuth

logger = logging.getLogger(__name__)
logging.basicConfig(level=logging.DEBUG)

# Example integration test script
# This is an initial test that illustrates a proposed test sequence. This is provisional until more
# formal GA4GH integration testing can be implemented.

rest_host = "http://localhost:8888/"
drs_host = "http://localhost:8080/"
irods_user = "test1"
irods_password = "test"
drs_bundle_id = "9ac9a341-b887-454b-9b24-28ac251147a7" # insert your own bundle id!


def test_sequence():
    logger.info("do auth")

    # get the auth token from the rest-api

    url = rest_host + "token"
    auth = HTTPBasicAuth(irods_user, irods_password)

    payload = {}
    headers = {
        'Accept': 'application/json',
    }

    response = requests.request("POST", url, headers=headers, data=payload, auth=auth)
    if response.status_code != 200:
        raise Exception("Invalid auth response")

    logger.info("response: %s" % response)
    auth_token = str(response.content, 'UTF-8')
    logger.info("auth_token: %s" % auth_token)

    # exercise the service info endpoint

    logger.info("do service info endpoint")

    url = drs_host + "ga4gh/drs/v1/service-info"

    payload = {}
    headers = {
        'Accept': 'application/json',
        'Authorization': 'Bearer ' + auth_token
    }

    response = requests.request("GET", url, headers=headers, data=payload)

    logger.info("service_info response: %s" % response.content)
    service_info_json = json.loads(response.content)

    service_info_id = service_info_json["id"]
    logger.info("retrieved id: %s" % service_info_id)

    if service_info_id != "test_irods":
        raise Exception("invalid service info")

    # get the drs bundle

    logger.info("get drs bundle")

    url = drs_host + "ga4gh/drs/v1/objects/" + drs_bundle_id

    payload = {}
    headers = {
        'Accept': 'application/json',
        'Authorization': 'Bearer ' + auth_token
    }

    response = requests.request("GET", url, headers=headers, data=payload)
    drs_bundle_json = json.loads(response.content)

    if drs_bundle_json["id"] != drs_bundle_id:
        raise Exception("unknown bundle returned")

    target_file_id = drs_bundle_json["contents"][0]["id"]
    target_file_name = drs_bundle_json["contents"][0]["name"]

    logger.info("target_file_id: %s" % target_file_id)
    logger.info("target_file_name: %s" % target_file_name)

    # now get the file given the drs id

    url = drs_host + "ga4gh/drs/v1/objects/" + target_file_id

    payload = {}
    headers = {
        'Accept': 'application/json',
        'Authorization': 'Bearer ' + auth_token
    }

    response = requests.request("GET", url, headers=headers, data=payload)
    drs_bundle_json = json.loads(response.content)

    if drs_bundle_json["id"] != target_file_id:
        raise Exception("unknown file returned")

    # now get the access method for irods-rest


    url = drs_host + "ga4gh/drs/v1/objects/" + target_file_id + "/access/irods-rest"

    payload = {}
    headers = {
        'Accept': 'application/json',
        'Authorization': 'Bearer ' + auth_token
    }

    response = requests.request("GET", url, headers=headers, data=payload)
    access_json = json.loads(response.content)

    logger.info("response with access_url: %s" % drs_bundle_json)

    access_url = access_json["url"]
    access_headers = access_json["headers"]
    api_key = access_headers[0].split(' ')[1]
    headers = {
        'X-API-KEY': api_key
    }

    # test file level access

    payload = {}

    response = requests.request("GET", access_url, headers=headers, data=payload)

    if response.status_code != 200:
        raise Exception("invalid response to file streaming")

# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    test_sequence()

