# invalid request format = 10
# summoner already registered = 15
# incorrect summoner/password combination = 20
# invalid response from riot servers = 25
# internal processing error = 30

invalid_request_format = {"error": 10, \
                          "message" : "invalid request format"}
summoner_already_registered = {"error": 15, \
                               "message" : "summoner already registered"}
incorrect_credentials = {"error": 20, \
                         "message" : "incorrect summoner/password combination"}
invalid_riot_response = {"error": 25, \
                         "message" : "invalid response from riot servers"}
internal_processing_error = {"error": 30, \
                             "message" : "internal processing error"}
