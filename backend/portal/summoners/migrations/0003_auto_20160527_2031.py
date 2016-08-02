# -*- coding: utf-8 -*-
# Generated by Django 1.9.6 on 2016-05-27 20:31
from __future__ import unicode_literals

import django.db.models.deletion
from django.db import migrations, models


class Migration(migrations.Migration):
    dependencies = [
        ('summoners', '0002_auto_20160527_2028'),
    ]

    operations = [
        migrations.AlterField(
            model_name='summoner',
            name='user',
            field=models.OneToOneField(blank=True, null=True, on_delete=django.db.models.deletion.CASCADE,
                                       to='summoners.User'),
        ),
    ]
