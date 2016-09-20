# -*- coding: utf-8 -*-
# Generated by Django 1.10.1 on 2016-09-20 21:38
from __future__ import unicode_literals

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('stats', '0001_initial'),
    ]

    operations = [
        migrations.AlterModelOptions(
            name='matchstats',
            options={},
        ),
        migrations.AlterModelOptions(
            name='seasonstats',
            options={},
        ),
        migrations.AlterUniqueTogether(
            name='matchstats',
            unique_together=set([('region', 'summoner_id', 'match_id')]),
        ),
        migrations.AlterUniqueTogether(
            name='seasonstats',
            unique_together=set([('region', 'summoner_id', 'champion')]),
        ),
    ]
