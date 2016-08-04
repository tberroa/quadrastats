# -*- coding: utf-8 -*-
# Generated by Django 1.9.6 on 2016-08-04 21:27
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('stats', '0019_auto_20160706_1933'),
    ]

    operations = [
        migrations.AlterField(
            model_name='matchstats',
            name='champion',
            field=models.IntegerField(),
        ),
        migrations.AlterField(
            model_name='matchstats',
            name='lane',
            field=models.CharField(max_length=8),
        ),
        migrations.AlterField(
            model_name='matchstats',
            name='match_creation',
            field=models.BigIntegerField(),
        ),
        migrations.AlterField(
            model_name='matchstats',
            name='match_duration',
            field=models.BigIntegerField(),
        ),
        migrations.AlterField(
            model_name='matchstats',
            name='match_id',
            field=models.BigIntegerField(),
        ),
        migrations.AlterField(
            model_name='matchstats',
            name='region',
            field=models.CharField(max_length=4),
        ),
        migrations.AlterField(
            model_name='matchstats',
            name='role',
            field=models.CharField(max_length=16),
        ),
        migrations.AlterField(
            model_name='matchstats',
            name='spell1',
            field=models.IntegerField(),
        ),
        migrations.AlterField(
            model_name='matchstats',
            name='spell2',
            field=models.IntegerField(),
        ),
        migrations.AlterField(
            model_name='matchstats',
            name='summoner_id',
            field=models.BigIntegerField(),
        ),
        migrations.AlterField(
            model_name='matchstats',
            name='summoner_key',
            field=models.CharField(max_length=32),
        ),
        migrations.AlterField(
            model_name='matchstats',
            name='summoner_name',
            field=models.CharField(max_length=32),
        ),
        migrations.AlterField(
            model_name='seasonstats',
            name='region',
            field=models.CharField(max_length=4),
        ),
        migrations.AlterField(
            model_name='seasonstats',
            name='summoner_id',
            field=models.BigIntegerField(),
        ),
        migrations.AlterField(
            model_name='seasonstats',
            name='summoner_key',
            field=models.CharField(max_length=32),
        ),
        migrations.AlterField(
            model_name='seasonstats',
            name='summoner_name',
            field=models.CharField(max_length=32),
        ),
    ]
