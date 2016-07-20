# -*- coding: utf-8 -*-
# Generated by Django 1.9.6 on 2016-07-06 19:33
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('stats', '0018_auto_20160626_1906'),
    ]

    operations = [
        migrations.DeleteModel(
            name='SeasonStatsChampion',
        ),
        migrations.RemoveField(
            model_name='seasonstats',
            name='season',
        ),
        migrations.AddField(
            model_name='seasonstats',
            name='champion',
            field=models.IntegerField(default=0),
        ),
        migrations.AddField(
            model_name='seasonstats',
            name='region',
            field=models.CharField(default='', max_length=4),
        ),
        migrations.AddField(
            model_name='seasonstats',
            name='summoner_id',
            field=models.BigIntegerField(default=0),
        ),
        migrations.AddField(
            model_name='seasonstats',
            name='summoner_key',
            field=models.CharField(default='', max_length=32),
        ),
        migrations.AddField(
            model_name='seasonstats',
            name='summoner_name',
            field=models.CharField(default='', max_length=32),
        ),
    ]