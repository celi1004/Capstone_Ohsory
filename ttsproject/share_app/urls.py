from django.urls import path, include
from .views import ShareInfoList, MyShareInfo, ShareAPI, MyShareAPI, ShareListAPI, ShareAuthAPI, ShareInfoNotiAPI

urlpatterns = [
    path("share/create", ShareAPI.as_view()),
    path("share/<int:pk>/delete", ShareAPI.as_view()),
    path("share/myshare", MyShareAPI.as_view()),
    path("share/share-list", ShareListAPI.as_view()),
    path("share/<int:pk>/auth-update", ShareAuthAPI.as_view()),
    path("share-info/notification", ShareInfoNotiAPI.as_view()),
    path("share-info/<int:pk>/approve", ShareInfoNotiAPI.as_view()),
    path("share-info/myshare", MyShareInfo.as_view()),
    path("share-info/share-list", ShareInfoList.as_view())
]