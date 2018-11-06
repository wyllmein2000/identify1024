# coding:utf-8
from flask import Flask
from flask import Flask,render_template,request,redirect,url_for
import base64
import urllib, urllib2, base64

app = Flask('hello')

@app.route('/hello/')
def hello():
  return 'Hello World!'

@app.route('/upload/<id_str>', methods=['POST', 'GET'])
def upload(id_str):
  return id_str

@app.route('/upload1', methods=['POST', 'GET'])
def upload1():
  return request.args.get('a')


@app.route('/register', methods=['POST'])
def register():
  print request.headers
  print request.form
  print request.form['name']
  print request.form.get('name')
  print request.form.getlist('name')
  print request.form.get('nickname', default='little apple')
  return 'welcome'

@app.route('/pic', methods=['POST'])
def pic():
  print request.headers
  print request.form
  img = request.form['base64']
  result = baidu(img)
  return 'welcome' + result

def baidu(img):
  base='https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic'
  #base='https://aip.baidubce.com/rest/2.0/ocr/v1/numbers'
  access_token = '24.6dbdb9e2322738ad894a9ebd360daaad.2592000.1543505765.282335-14620708'
  url = base + '?access_token=' + access_token

  params = {"image": img}
  params = urllib.urlencode(params)
  request = urllib2.Request(url, params)
  request.add_header('Content-Type', 'application/x-www-form-urlencoded')
  response = urllib2.urlopen(request)
  content = response.read()
  return content

app.run(host='0.0.0.0', port=9999, debug=True)
