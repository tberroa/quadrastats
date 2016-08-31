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
        return self.email + "," + str(self.created)


class Summoner(models.Model):
    user = models.OneToOneField(User, null=True, blank=True, on_delete=models.SET_NULL)
    region = models.CharField(max_length=4)
    key = models.CharField(max_length=32)
    name = models.CharField(max_length=32)
    summoner_id = models.BigIntegerField()
    tier = models.CharField(max_length=16)
    division = models.CharField(max_length=4)
    lp = models.IntegerField()
    wins = models.IntegerField()
    losses = models.IntegerField()
    series = models.CharField(max_length=8, default="", blank=True)
    profile_icon = models.IntegerField()
    friends = models.CharField(max_length=1024, default="", blank=True)
    accessed = models.DateTimeField(null=True, blank=True)

    def save(self, *args, **kwargs):
        self.accessed = datetime.now()
        super(Summoner, self).save(*args, **kwargs)

    def __str__(self):
        return self.region + "," + self.name
