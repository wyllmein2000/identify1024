import requests
import base64

f = open(r'/Users/liangxin.wll/221.jpg', 'rb')
img = base64.b64encode(f.read())

user_info = {'base64': img}
r = requests.post("http://10.97.184.63:9999/pic", data=user_info)

print(r.text)
