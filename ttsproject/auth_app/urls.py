from django.urls import path, include
from .views import RegistrationAPI, LoginAPI, UserAPI, ProgressUpdateAPI

urlpatterns = [
    path("auth/register/", RegistrationAPI.as_view()),
    path("auth/login/", LoginAPI.as_view()),
    path("auth/user/", UserAPI.as_view()),
    path("auth/progress", ProgressUpdateAPI.as_view())
]