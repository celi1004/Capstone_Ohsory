"""ttsproject URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/3.0/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path, include, re_path
from rest_framework import routers, permissions

from drf_yasg.views import get_schema_view
from drf_yasg import openapi
from django.conf import settings
from django.conf.urls.static import static

router = routers.DefaultRouter()

schema_view = get_schema_view(
    openapi.Info(
        title="Oh!sory API",
        default_version='v1',
        description="API for Oh!sory, personal TTS service",
        terms_of_service="https://www.google.com/policies/terms/",
        contact=openapi.Contact(email="pjpp8588@naver.com"),
        license=openapi.License(name="BSD License"),
    ),
    validators=['flex'],
    public=True,
    permission_classes=(permissions.AllowAny,),
)

urlpatterns = [
    path('auth_app/', include('auth_app.urls')),
    path('share_app/', include('share_app.urls')),
    path('auth_app/auth', include('knox.urls')),
    path('tts_app/', include('tts_app.urls')),
    path('', include(router.urls)),
    re_path('admin/', admin.site.urls),
]

if settings.DEBUG:
    urlpatterns += [
        re_path(r'^swagger(?P<format>\.json|\.yaml)$', schema_view.without_ui(cache_timeout=0), name='schema-json'),
        re_path(r'^swagger/$', schema_view.with_ui('swagger', cache_timeout=0), name='schema-swagger-ui'),
        re_path(r'^redoc/$', schema_view.with_ui('redoc', cache_timeout=0), name='schema-redoc')
    ]+ static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)