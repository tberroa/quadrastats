from django.shortcuts import render

from rest_framework.views import APIView
from rest_framework.response import Response

class GetRecentStats(APIView):
    def post(self, request, format=None):
        data = request.data
        return Response(data)

class GetSeasonStats(APIView):
    def post(self, request, format=None):
        data = request.data
        return Response(data)

class GetChampionStats(APIView):
    def post(self, request, format=None):
        data = request.data
        return Response(data)
