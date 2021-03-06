package com.ycbjie.android.view.activity

import android.graphics.Color
import android.graphics.Typeface
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.ycbjie.android.presenter.AndroidPresenter
import com.ycbjie.android.view.fragment.AndroidHomeFragment
import com.ycbjie.android.view.fragment.AndroidKnowledgeFragment
import com.ycbjie.android.view.fragment.AndroidProfileFragment
import com.ycbjie.android.view.fragment.AndroidProjectFragment
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.ActivityUtils
import com.flyco.tablayout.CommonTabLayout
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.ycbjie.android.R
import com.ycbjie.android.base.KotlinConstant
import com.ycbjie.android.model.bean.BannerBean
import com.ycbjie.library.base.adapter.BasePagerAdapter
import com.ycbjie.library.base.mvp.BaseActivity
import com.ycbjie.library.constant.Constant
import com.ycbjie.library.model.entry.TabEntity
import com.ycbjie.library.arounter.ARouterConstant
import com.ycbjie.library.arounter.ARouterUtils
import com.ycbjie.library.web.view.WebViewActivity
import kotlinx.android.synthetic.main.base_android_bar.*
import java.lang.ref.WeakReference
import java.util.ArrayList

/**
 * <pre>
 *     @author 杨充
 *     blog  :
 *     time  : 2018/01/30
 *     desc  : kotlin学习：
 *     revise:
 * </pre>
 */
@Route(path = ARouterConstant.ACTIVITY_ANDROID_ACTIVITY)
class AndroidActivity : BaseActivity<AndroidPresenter>(){

    /**
     * var关键字声明可变属性
     * val关键字声明只读属性
     * 属性的类型在后面，变量名在前面，中间加冒号和空格
     */
    var presenter : AndroidPresenter? = null

    /**
     * 定义局部变量和常量
     * 0. Kotlin声明变量与Java声明变量有些不一样，Java变量类型在前，变量名在后，
     *   而Kotlin则相反，变量名在前，变量类型在后，中间加:(冒号)
     *   并且Kotlin可以自动判断变量的类型。
     *
     * 1.常量
     *      常量使用val关键字，val代表只读
     *
     * 2.变量
     *      变量使用var关键字，val代表可变
     *
     * val是线程安全的，并且必须在定义时初始化，所以不需要担心 null 的问题
     * 强烈推荐能用val的地方就用val
     */
    private var mTvTitleLeft :TextView?=null
    private var mLlTitleMenu :FrameLayout?=null
    private var mToolbarTitle :TextView?=null
    private var mIvRightImg :ImageView?=null
    private var viewPager: ViewPager? = null
    private var ctlTable : CommonTabLayout? =null
    /**
     * 创建集合
     *
     */
    private var fragments = mutableListOf<Fragment>()
    private var pageAdapter : BasePagerAdapter? = null
    private var index: Int = 0      //定义具体的类型

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.kotlin_menu_main,menu)
        menu?.add(0, 1, 0, "登录玩Android")
        menu?.add(0, 2, 1, "开发作者介绍")
        menu?.add(0, 3, 2, "分享此软件")
        menu?.add(0, 4, 3, "开源项目介绍")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.actionSearch -> {
                AndroidSearchActivity.lunch(this)
            }
            R.id.actionUrlNav -> {
                NavWebsiteActivity.lunch(this)
            }
            1 -> {
                //ToastUtils.showRoundRectToast("登录玩Android")
                ActivityUtils.startActivity(AndroidLoginActivity::class.java)
            }
            2 -> {
                //ToastUtils.showRoundRectToast("开发作者介绍")
                WebViewActivity.lunch(this, Constant.GITHUB, "我的GitHub")
            }
            3 -> {
                //ToastUtils.showRoundRectToast("分享此软件")
                WebViewActivity.lunch(this, Constant.ZHI_HU, "我的知乎")
            }
            4 -> {
                //ToastUtils.showRoundRectToast("开源项目介绍")
                ARouterUtils.navigation(ARouterConstant.ACTIVITY_OTHER_ABOUT_ME)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 定义函数
     * 1.与Java定义函数的区别在于：Kotlin在定义函数的时候要加个fun关键词，函数的返回值前后不同，Java的在前面，kotlin在后面
     * 2.如果一个函数只有一个并且是表达式函数体并且是返回类型自动推断的话，可以直接这样写
     *   fun getResult(a: Int, b: Int) = a + b
     * 3.如果函数返回一个无意义的值，相当于Java的void，则可以这样写
     *   fun initView(){}
     */
    override fun getContentView(): Int {
        return R.layout.activity_wan_android
    }


    /**
     * 1. ?: Elvis 操作符
     *       val l = b?.length ?: -1
     *       如果 ?: 左侧表达式非空，elvis 操作符就返回其左侧表达式，否则返回右侧表达式。
     *       注意:当且仅当左侧为空时，才会对右侧表达式求值。
     *
     * 2. !! 操作符
     *      这是为空指针爱好者准备的，非空断言运算符（!!）将任何值转换为非空类型，若该值为空则抛出异常
     *      能不用!!操作符就不要用。。。
     */
    override fun initView() {
        StateAppBar.setStatusBarColor(this, resources.getColor(R.color.colorTheme))
        initFindViewById()
        initToolBar()
        initFragment()
        initTabLayout()
    }


    /**
     * kotlin可以直接通过id找到控件，之前的findviewbyid就可以不用写了
     * 要先在module的build.gradle中配置：apply plugin: 'kotlin-android-extensions'
     * AndroidStudio会自动引用：import kotlinx.android.synthetic.main.activity_android_login.*
     *
     * 自定义控件，或者需要前缀view.findViewById的就需要findViewById呢……
     */
    private fun initFindViewById() {
        mTvTitleLeft = findViewById(R.id.tv_title_left)
        mLlTitleMenu = findViewById(R.id.ll_title_menu)
        mToolbarTitle = findViewById(R.id.toolbar_title)
        mIvRightImg = findViewById(R.id.iv_right_img)
        viewPager = findViewById(R.id.vp_pager)
        ctlTable = findViewById(R.id.ctl_table)
        mTvTitleLeft!!.visibility = View.VISIBLE
        mTvTitleLeft!!.textSize = 16.0f
        mTvTitleLeft!!.typeface = Typeface.DEFAULT
        mLlTitleMenu!!.visibility = View.GONE
        mIvRightImg!!.visibility = View.VISIBLE
    }


    override fun initListener() {

    }


    override fun initData() {

    }

    private fun initToolBar() {
        mTvTitleLeft?.text = "首页"
        ll_title_menu.visibility = View.GONE
        toolbar_title.visibility = View.GONE
        toolbar.run {
            setSupportActionBar(toolbar)
            supportActionBar?.title = ""
            setTitleTextColor(Color.WHITE)
        }
    }

    /**
     * Kotlin不需要使用new关键字，直接写：类()
     */
    private fun initFragment() {
        fragments.add(AndroidHomeFragment())
        fragments.add(AndroidKnowledgeFragment())
        fragments.add(AndroidProjectFragment())
        fragments.add(AndroidProfileFragment())

        pageAdapter = BasePagerAdapter(supportFragmentManager, fragments)
        viewPager.run {
            this!!.adapter = pageAdapter
            addOnPageChangeListener(PagerChangeListener(this@AndroidActivity))
            offscreenPageLimit = fragments.size
        }
    }

    private fun initTabLayout() {
        val mTabEntities = ArrayList<CustomTabEntity>()
        val mIconUnSelectIds = this.resources.obtainTypedArray(R.array.android_tab_un_select)
        val mIconSelectIds = this.resources.obtainTypedArray(R.array.android_tab_select)
        val mainTitles = this.resources.getStringArray(R.array.android_title)
        for (i in mainTitles.indices) {
            val unSelectId = mIconUnSelectIds.getResourceId(i, R.drawable.ic_tab_main_art_uncheck)
            val selectId = mIconSelectIds.getResourceId(i, R.drawable.ic_tab_main_art_checked)
            mTabEntities.add(TabEntity(mainTitles[i], selectId, unSelectId))
        }
        mIconUnSelectIds.recycle()
        mIconSelectIds.recycle()

        ctlTable?.setTabData(mTabEntities)
        ctlTable?.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                if(position>=0 && position<fragments.size){
                    viewPager!!.currentItem = position
                }
            }

            override fun onTabReselect(position: Int) {

            }
        })
    }

    class PagerChangeListener constructor(activity: AndroidActivity) : ViewPager.OnPageChangeListener {
        private var weakActivity: WeakReference<AndroidActivity>? = null
        init {
            this.weakActivity = WeakReference(activity)
        }

        override fun onPageScrollStateChanged(state: Int) {}

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            val activity: AndroidActivity? = weakActivity?.get()
            if (activity != null) {
                activity.index = position
                activity.selectByIndex(position)
            }
        }
    }

    /**
     * 使用when表达式
     * when表达式就相当于Java的switch表达式，省去了case和break，并且支持各种类型。
     *
     * 控制流(Control Flow)：Kotlin的控制流有if``when``for``while四种
     */
    private fun selectByIndex(position: Int) {
        mTvTitleLeft?.visibility = View.VISIBLE
        when (position){
            0 ->{
                ctlTable?.currentTab = 0
                mTvTitleLeft?.text = "首页"
                //supportActionBar!!.title = "首页"
            }
            1 ->{
                ctlTable?.currentTab = 1
                mTvTitleLeft?.text = "体系"
                //supportActionBar!!.title = "项目"
            }
            2 ->{
                ctlTable?.currentTab = 2
                mTvTitleLeft?.text = "项目"
                //supportActionBar!!.title = "博客"
            }
            3 ->{
                ctlTable?.currentTab = 3
                mTvTitleLeft?.text = "用户"
                //supportActionBar!!.title = "我的"
            }
            else -> {
                // 默认，相当于switch中default
                print("x is neither 1 nor 2")
                print(Foo(10, 20))
                print(Foo(10, 20))
                KotlinConstant.NEW_ID
                KotlinConstant.IS_CACHE
                this.toast("拓展函数")
            }
        }
    }


    // 一个简单的数据类
    // 用于重载运算符的所有函数都必须使用operator关键字标记。
    // 算术运算符：https://www.jianshu.com/p/d445209091f0
    data class Foo(private val x: Int, private val y: Int) {
        // a + b
        operator fun plus(other: Foo) {
            Foo(x + other.x, y + other.y)
        }
        // a * b
        operator fun times(other: Foo): Foo = Foo(x * other.x, y * other.y)
        // a % b
        operator fun rem(other: Foo): Foo = Foo(x % other.x, y % other.y)
        // a / b
        operator fun div(other: Foo): Foo = Foo(x % other.x, y % other.y)
        // a - b
        operator fun minus(other: Foo): Foo = Foo(x % other.x, y % other.y)

        // 支持运算符两边互换使用
        operator fun Double.times(other: Foo): Foo = Foo((this * other.x).toInt(), (this * other.y).toInt())

        //?: 的意思是，左边的表达式没有成功，则使用右边的结果；
        //如下，person是null,所以person?.name不会执行，所以最终a == "null"
        var bannerBean : BannerBean? = null
        var a = bannerBean?.title ?: "null"
    }

    /**
     * 关键字
     * object           为同时声明一个类及其实例
     * typealias        类型别名为现有类型提供替代名称
     * as               是一个中缀操作符，as是不安全的转换操作符，如果as转换失败，会抛出一个异常，这就是不安全的。
     * as?              as?与as类似，也是转换操作符，但是与as不同的是，as?是安全的，也就是可空的，可以避免抛出异常，在转换失败是会返回null
     * fun              表示声明一个函数
     * in               用于指定for循环中迭代的对象
     * !in              表示与in相反，用作中缀操作符以检查一个值不属于一个区间、一个集合或者其他定义contains方法的实体。
     * is和!is          是否符合给定类型，类似与Java的instanceOf，is操作符或其否定形式!is来检查对象是否符合给定类型
     * constructor      声明一个主构造函数或次构造函数
     * init             主构造函数不能包含任何的代码。初始化的代码可以放到以init关键字作为前缀的初始化块中：
     * where            用于指定泛型多个类型的上界约束
     *
     */


    /**
     * 拓展函数，此处duration已经赋了默认值，所以这个参数可传可不传。
     */
    fun FragmentActivity.toast(message: CharSequence,duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }


}
