import numpy as np
import os
from glob import glob
from skimage import io, transform


def rgb2grey(image):
    gray = image[:, :, 0] * 0.299 + image[:, :, 1] * 0.587 + image[:, :, 2] * 0.114 + 0.5
    #gray = np.sum(image, axis=2) / 3.0
    gray = gray.astype(np.uint8)
    return gray

def grey2bw(gray, threshold):
    bw = gray
    idx1 = gray <= threshold
    idx2 = gray >= threshold
    bw[idx1] = 255
    bw[idx2] = 0
    return bw;

def rgb2bw(image, threshold):
    gray = rgb2grey(image)
    idx1 = gray <= threshold
    idx2 = gray >= threshold
    gray[idx1] = 255
    gray[idx2] = 0
    return gray;


def load_image(image_path, image_height, image_width):
    file_name=glob(image_path+"/*jpg")
    sample = []
    for file in file_name:
        pic = io.imread(file)#.astype(np.float32)         
        pic = transform.resize(pic, (image_height, image_width)) * 255
        pic = pic.astype(np.uint8)
        sample.append(pic[:,:,:3])
 
    sample = np.array(sample)
    return sample

def load_image_x(imgDir, nh, nw):

    data = []
    label = []
    n = 0

    for s in os.listdir(imgDir):
        if s.isdigit() == False:
            continue
        subdir = os.path.join(imgDir, s)
        samples = load_image(subdir, nh, nw)
        t = np.ones(len(samples), dtype=np.uint8) * int(s)
        n = n + len(samples)
        print(samples.shape)
        #data.append(samples)
        #label.append(t)
        data.extend(samples)
        label.extend(t)

    data = np.array(data)
    data = np.reshape(data, (n, nh, nw, 3))

    label = np.array(label)
    label = np.reshape(label, n)
    return data, label

def load_image_png(image_path, image_height, image_width):
    file_name=glob(image_path+"/*png")
    sample = []
    for file in file_name:
        pic = io.imread(file)#.astype(np.float32)         
        pic = transform.resize(pic, (image_height, image_width)) * 255
        pic = pic.astype(np.uint8)
        sample.append(pic)
 
    sample = np.array(sample)
    return sample

def preprocess(image, m, gc):
    out = []
    (n, nh, nw, nc) = image.shape
    for k in range(n):
        x = image[k, :, m:nw-m, :]
        x = rgb2bw(x, gc)
        x = x / 255.0
        out.append(x)
    out = np.array(out)
    return out
