# -*- coding: utf-8 -*-
# Generated by Django 1.9.6 on 2016-06-04 17:20
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('summoners', '0011_auto_20160602_1541'),
    ]

    operations = [
        migrations.AlterField(
            model_name='summoner',
            name='riot_id',
            field=models.BigIntegerField(default=0),
        ),
    ]