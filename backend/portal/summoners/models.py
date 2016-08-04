from datetime import datetime
from django.db import models


class User(models.Model):
    email = models.EmailField(max_length=256)
    password = models.CharField(max_length=256)
    created = models.DateTimeField(null=True, blank=True)

    def save(self, *args, **kwargs):
        if not self.id:
            self.created = datetime.now()
        super(User, self).save(*args, **kwargs)

    def __str__(self):
        return self.email


class Summoner(models.Model):
    user = models.OneToOneField(User, null=True, blank=True, on_delete=models.CASCADE)
    region = models.CharField(max_length=4)
    key = models.CharField(max_length=32)
    name = models.CharField(max_length=32)
    summoner_id = models.BigIntegerField(default=0)
    tier = models.CharField(max_length=16)
    division = models.CharField(max_length=4)
    wins = models.IntegerField(default=0)
    losses = models.IntegerField(default=0)
    series_progress = models.CharField(max_length=8, default="", blank=True)
    profile_icon = models.IntegerField(default=0)
    friends = models.CharField(max_length=1024, default="", blank=True)
    modified = models.DateTimeField(null=True, blank=True)

    def save(self, *args, **kwargs):
        self.modified = datetime.now()
        super(Summoner, self).save(*args, **kwargs)

    def __str__(self):
        return self.region + "," + self.name
