from django.urls import path, include
from .views import RegistrationAPI, LoginAPI, UserAPI, ProgressAPI, FcmKeyUpdateAPI, ModelAPI

urlpatterns = [
    path("auth/register", RegistrationAPI.as_view()),
    path("auth/login", LoginAPI.as_view()),
    path("auth/user", UserAPI.as_view()),
    path("auth/profile/<int:user_pk>/get-progress", ProgressAPI.as_view()),
    path("auth/profile/<int:user_pk>/fcm-key-update", FcmKeyUpdateAPI.as_view()),
    path("auth/profile/model", ModelAPI.as_view())
]