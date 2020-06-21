from __future__ import absolute_import

from ttsproject.celery import app
import requests
import json

#FCM 보내기
@app.task
def send_fcm_notification(ids, title, body):

    url = "https://fcm.googleapis.com/fcm/send"

    headers = {
        'Authorization': 'key=AAAATbUzWAk:APA91bEEuV5XjsItkpqBSXAXKNVo2_A4U1ZnzCRK2tfrqwQVDTPcSPE20reyZRpaKoqaRey8bSAMnX2xEbvKRZxneih8LzrcBvzq1hgT2AsBFqSL7CGzLeuxNTeEvrJbRrofhs0k6407',
        'Content-Type': 'application/json; UTF-8',
    }

    content = {
        'to': ids,
        'notification':{
            'title': title,
            'body': body
        }
    }

    response = requests.post(url, data=json.dumps(content), headers=headers)

    print(response)