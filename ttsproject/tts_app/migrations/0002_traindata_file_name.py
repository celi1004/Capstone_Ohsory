# Generated by Django 3.0.5 on 2020-06-05 18:18

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('tts_app', '0001_initial'),
    ]

    operations = [
        migrations.AddField(
            model_name='traindata',
            name='file_name',
            field=models.CharField(max_length=20, null=True),
        ),
    ]
