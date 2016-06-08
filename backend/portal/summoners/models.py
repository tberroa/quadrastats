from datetime import datetime

from django.db import models

class User(models.Model):
    created = models.DateTimeField(null = True, blank = True)
    password = models.CharField(max_length = 256, null = True, blank = True)

    def save(self, *args, **kwargs):
        if not self.id:
            self.created = datetime.now()
        super(User, self).save(*args, **kwargs)

class Summoner(models.Model):
    user = models.OneToOneField(User, null = True, blank = True, on_delete=models.CASCADE)
    region = models.CharField(max_length = 128)
    key = models.CharField(max_length = 128)
    name = models.CharField(max_length = 128)
    summoner_id = models.BigIntegerField(default = 0)
    profile_icon = models.IntegerField(default = 0)
    friends = models.CharField(max_length = 255, null = True, blank = True)
    modified = models.DateTimeField(null = True, blank = True)

    def save(self, *args, **kwargs):
        self.modified = datetime.now()
        super(Summoner, self).save(*args, **kwargs)

    def __str__(self):
        return self.region + "," + self.name
    
