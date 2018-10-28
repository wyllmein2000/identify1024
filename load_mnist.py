import tensorflow as tf

def gray2binary(image, threshold):
    idx1 = image <= threshold
    idx2 = image >= threshold
    image[idx1] = 0
    image[idx2] = 1.0
    return image

def reducedata(x, y):
    idx = (y < 3) | (y == 4)
    x = x[idx,:,:]
    y = y[idx]
    y[y == 4] = 3
    return x, y

def loadmnist():
    mnist = tf.keras.datasets.mnist

    (x_train, y_train),(x_test, y_test) = mnist.load_data()
    x_train, x_test = x_train / 255.0, x_test / 255.0

    x_train_b, y_train_b = reducedata(x_train, y_train)
    x_test_b, y_test_b = reducedata(x_test, y_test)

    x_train_b = gray2binary(x_train_b, 0.5)
    x_test_b = gray2binary(x_test_b, 0.5)

    return (x_train_b, y_train_b), (x_test_b, y_test_b)
