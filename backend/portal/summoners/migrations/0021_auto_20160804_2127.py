# -*- coding: utf-8 -*-
# Generated by Django 1.9.6 on 2016-08-04 21:27
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('summoners', '0020_auto_20160804_1817'),
    ]

    operations = [
        migrations.RenameField(
            model_name='summoner',
            old_name='modified',
            new_name='accessed',
        ),
        migrations.AlterField(
            model_name='summoner',
            name='losses',
            field=models.IntegerField(),
        ),
        migrations.AlterField(
            model_name='summoner',
            name='profile_icon',
            field=models.IntegerField(),
        ),
        migrations.AlterField(
            model_name='summoner',
            name='summoner_id',
            field=models.BigIntegerField(),
        ),
        migrations.AlterField(
            model_name='summoner',
            name='wins',
            field=models.IntegerField(),
        ),
    ]