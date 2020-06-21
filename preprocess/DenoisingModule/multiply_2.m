 clear;
 clc;
 
% path = 'C:/Users/junhy/Desktop/mhstt/';
path = 'C:/Users/junhy/Documents/2019/vsCodeTest/DenoisingModule/';
% C:\Users\junhy\Desktop\mhstt
% C:\Users\junhy\Desktop\jinyoung-1_denoised\jinyoung-1_denoised

files = dir(fullfile(path, '*.wav'));
nfiles = length(files);

for cnt1 = 17 : 17
    cnt1
    fullname = [path, files(cnt1).name];
    filename = files(cnt1).name;
    info = audioinfo([path, files(cnt1).name]);
    [y, Fs] = audioread(fullname);
    y = resample(y,16000,info.SampleRate);
    t = max(y);
    y = y./t;  
    y_r=y.*2;
    [pathstr,name,ext] = fileparts(files(cnt1).name);
    audiowrite([path, name, 'x2.wav'], y, 16000, 'BitsPerSample', 16);
end














