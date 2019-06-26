import os
from time import sleep
import unittest
from appium import webdriver
from appium.webdriver.common.touch_action import TouchAction

result = os.system('adb pull data/app/com.example.zhengmin.maidian-1/base.apk /Users/zhengmin/Downloads')
if 0 != result:
    os.system('adb pull data/app/com.example.zhengmin.maidian-2/base.apk /Users/zhengmin/Downloads')

sleep(1);
# Returns abs path relative to this file and not cwd

PATH = lambda p: os.path.abspath(
    os.path.join(os.path.dirname(__file__), p)
)

class SimpleAndroidTests(unittest.TestCase):
    def setUp(self):
        desired_caps = {}
        desired_caps['platformName'] = 'Android'
        desired_caps['platformVersion'] = '6.0'
        desired_caps['deviceName'] = 'Android Emulator'
        desired_caps['app'] = PATH(
            '/Users/zhengmin/Downloads/base.apk'
        )
        self.driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)

    def tearDown(self):
        # end the session
        self.driver.quit()

    def test_find_elements(self):

