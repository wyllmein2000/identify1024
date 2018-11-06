private String recognize(File file) {
        try {
            //String access_token = getAccessToken();
            //System.out.println("Get access_token:" + access_token);
            String access_token = "24.2c2eff0d301a56f6a20a512b72f47246.2592000.1543543093.282335-14620359";
            //读取图片字节数组
            InputStream fileInputStream = new FileInputStream(file);
            byte[] data = new byte[fileInputStream.available()];
            fileInputStream.read(data);
            fileInputStream.close();
            URL realUrl = new URL("https://aip.baidubce.com/rest/2.0/ocr/v1/numbers?recognize_granularity=small");
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            String enCode = Base64.getEncoder().encodeToString(data);


            String param = "access_token=" + URLEncoder.encode(access_token) + "&image=" + URLEncoder.encode(enCode,
                "UTF-8");
            connection.setRequestMethod("POST");
            connection.connect();
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.writeBytes(param);
            dos.flush();
            dos.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            //System.out.println("result:" + result);

            Pattern pt = Pattern.compile("words\": \"([0-9])\"");
            Matcher match = pt.matcher(result);
            if (match.find()) {
                return match.group(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return FOUR;
    }

    private String getAccessToken() {
        String getAccessTokenUrl
            = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id"
            + "=E7088q05rrbjyrwXDusTRI4c&client_secret=fhrKD2RljDSPEd6aKpivtDfw8pkiWoLS";
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }

            //System.out.println(" result access_token:" + result);
            /**
             * 返回结果示例
             */
            Pattern pt = Pattern.compile("access_token\":\"(.*)\"");
            Matcher match = pt.matcher(result);
            if (match.find()) {
                //System.out.println("access_token:" + match.group(1));
                return match.group(1);
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
