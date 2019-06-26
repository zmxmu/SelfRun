package com.example.zhengmin.maidian.activity;

/**
 * Created by zhengmin on 2018/3/20.
 */

public class Const {
    public static final String PY_CONTENT_END = "\nif __name__ == '__main__':\n" +
            "    suite = unittest.TestLoader().loadTestsFromTestCase(SimpleAndroidTests)\n" +
            "    unittest.TextTestRunner(verbosity=2).run(suite)" ;
}
