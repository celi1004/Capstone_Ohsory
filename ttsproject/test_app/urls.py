from django.urls import path, include

app_name = 'test_app'

urlpatterns = [
    path('', include('rest_framework.urls', namespace='rest_framework_category'))
]