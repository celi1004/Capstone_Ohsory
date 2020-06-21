from rest_framework import serializers
from .models import Share, Share_info

class ShareInfoSerializer(serializers.ModelSerializer):
    
    class Meta:
        model = Share_info
        fields = "__all__"

class MyShareInfoSerializer(serializers.ModelSerializer):
    
    share_infos = ShareInfoSerializer(many=True, read_only=True)

    class Meta:
        model = Share
        fields = ("id", "sharee_name", "share_infos")

class ShareInfoListSerializer(serializers.ModelSerializer):
    
    share_infos = ShareInfoSerializer(many=True, read_only=True)

    class Meta:
        model = Share
        fields = ("id", "sharer_name", "share_infos")

class ShareInfoApproveSerializer(serializers.ModelSerializer):
    class Meta:
        model = Share_info
        fields = ("id", "if_approve")

class ShareSerializer(serializers.ModelSerializer):

    class Meta:
        model = Share
        fields = ("id", "sharer_name", "sharee_name", "listening_noti", "download_noti", "download_auth")

class ShareListSerializer(serializers.ModelSerializer):

    class Meta:
        model = Share
        fields = ("id", "sharer_name")

class ShareAuthSerializer(serializers.ModelSerializer):

    class Meta:
        model = Share
        fields = ("id", "listening_noti", "download_noti", "download_auth")

