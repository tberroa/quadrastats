# -*- coding: utf-8 -*-
# Generated by Django 1.9.6 on 2016-05-27 18:42
from __future__ import unicode_literals

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Summoner',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=128)),
                ('profile_icon', models.IntegerField()),
                ('last_accessed', models.DateTimeField()),
                ('last_updated', models.DateTimeField()),
            ],
        ),
        migrations.CreateModel(
            name='User',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('password', models.CharField(max_length=256)),
            ],
        ),
        migrations.AddField(
            model_name='summoner',
            name='user',
            field=models.OneToOneField(on_delete=django.db.models.deletion.CASCADE, to='summoners.User'),
        ),
    ]
