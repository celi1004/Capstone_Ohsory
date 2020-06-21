import os

base_dir = 'Tacotron2-Wavenet-Korean-TTS'
dataset_dir = "datasets"
data_dir = "data"
print(os.path.isdir(os.path.join(base_dir, data_dir, '049F')))