package in.vikas.nothingvivo

import android.app.Activity
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LauncherActivity : Activity() {
    private val bg = Color.rgb(8, 9, 11)
    private val white = Color.rgb(244, 244, 241)
    private val muted = Color.rgb(145, 148, 151)
    private val red = Color.rgb(232, 61, 79)
    private lateinit var appGrid: GridLayout
    private lateinit var search: EditText
    private lateinit var countLabel: TextView
    private var allApps: List<ResolveInfo> = emptyList()
    private var showingAll = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var clock: TextView
    private lateinit var date: TextView

    private val clockTick = object : Runnable {
        override fun run() {
            val now = Date()
            clock.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
            date.text = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(now).uppercase(Locale.getDefault())
            handler.postDelayed(this, 30_000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        window.decorView.systemUiVisibility = 0
        allApps = loadApps()
        buildHome()
        handler.post(clockTick)
    }

    override fun onResume() {
        super.onResume()
        allApps = loadApps()
        if (::appGrid.isInitialized) renderApps(search.text?.toString().orEmpty())
    }

    override fun onDestroy() {
        handler.removeCallbacks(clockTick)
        super.onDestroy()
    }

    private fun loadApps(): List<ResolveInfo> {
        val intent = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
        return packageManager.queryIntentActivities(intent, 0)
            .filter { it.activityInfo.packageName != packageName }
            .sortedBy { it.loadLabel(packageManager).toString().lowercase(Locale.getDefault()) }
    }

    private fun buildHome() {
        val root = FrameLayout(this)
        root.setBackgroundColor(bg)
        root.addView(NothingBackdrop(this), FrameLayout.LayoutParams(-1, -1))

        val scroll = ScrollView(this)
        scroll.isFillViewport = true
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(30), dp(22), dp(24))
        }
        scroll.addView(content)
        root.addView(scroll)
        setContentView(root)

        val brandRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val brand = label("NOTHING", 13f, white, true).apply { letterSpacing = 0.22f }
        brandRow.addView(brand, LinearLayout.LayoutParams(0, -2, 1f))
        brandRow.addView(label("V / 01", 11f, red, true))
        content.addView(brandRow)

        val redLine = View(this).apply { setBackgroundColor(red) }
        val lineParams = LinearLayout.LayoutParams(dp(34), dp(3))
        lineParams.topMargin = dp(16)
        content.addView(redLine, lineParams)

        clock = label("00:00", 66f, white, true).apply {
            typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL)
            letterSpacing = -0.04f
        }
        val clockParams = LinearLayout.LayoutParams(-1, -2)
        clockParams.topMargin = dp(18)
        content.addView(clock, clockParams)

        date = label("", 11f, muted, true).apply { letterSpacing = 0.16f }
        val dateParams = LinearLayout.LayoutParams(-1, -2)
        dateParams.topMargin = dp(2)
        content.addView(date, dateParams)

        val slogan = label("MAKE IT SIMPLE. MAKE IT YOURS.", 10f, muted, true).apply {
            letterSpacing = 0.11f
        }
        val sloganParams = LinearLayout.LayoutParams(-1, -2)
        sloganParams.topMargin = dp(32)
        content.addView(slogan, sloganParams)

        val section = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val appsTitle = label("APPLICATIONS", 12f, white, true).apply { letterSpacing = 0.12f }
        section.addView(appsTitle, LinearLayout.LayoutParams(0, -2, 1f))
        countLabel = label("", 10f, muted, false)
        section.addView(countLabel)
        val sectionParams = LinearLayout.LayoutParams(-1, -2)
        sectionParams.topMargin = dp(22)
        content.addView(section, sectionParams)

        search = EditText(this).apply {
            hint = "Search apps"
            setHintTextColor(Color.rgb(112, 115, 119))
            setTextColor(white)
            textSize = 14f
            singleLine = true
            background = roundedBg(Color.rgb(24, 26, 29), dp(12))
            setPadding(dp(15), 0, dp(15), 0)
        }
        val searchParams = LinearLayout.LayoutParams(-1, dp(48))
        searchParams.topMargin = dp(14)
        content.addView(search, searchParams)

        appGrid = GridLayout(this).apply {
            columnCount = 4
            useDefaultMargins = false
        }
        val gridParams = LinearLayout.LayoutParams(-1, -2)
        gridParams.topMargin = dp(16)
        content.addView(appGrid, gridParams)

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                showingAll = true
                renderApps(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val footer = label("NOTHING × VIVO  •  COMMUNITY EDITION", 9f, muted, true).apply {
            gravity = Gravity.CENTER
            letterSpacing = 0.08f
        }
        val footerParams = LinearLayout.LayoutParams(-1, -2)
        footerParams.topMargin = dp(28)
        content.addView(footer, footerParams)

        renderApps("")
    }

    private fun renderApps(query: String) {
        if (!::appGrid.isInitialized) return
        appGrid.removeAllViews()
        val filtered = allApps.filter {
            it.loadLabel(packageManager).toString().contains(query, ignoreCase = true)
        }
        val visible = if (query.isBlank() && !showingAll) filtered.take(12) else filtered
        countLabel.text = "${visible.size} / ${allApps.size}"
        visible.forEach { info ->
            val item = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(dp(3), dp(10), dp(3), dp(10))
                isClickable = true
                isFocusable = true
                setOnClickListener { launchApp(info) }
            }
            val icon = ImageView(this).apply {
                setImageDrawable(monochrome(info.loadIcon(packageManager)))
                scaleType = ImageView.ScaleType.FIT_CENTER
                contentDescription = info.loadLabel(packageManager).toString()
            }
            item.addView(icon, LinearLayout.LayoutParams(dp(42), dp(42)))
            val name = label(info.loadLabel(packageManager).toString(), 10f, white, false).apply {
                gravity = Gravity.CENTER
                maxLines = 1
                ellipsize = android.text.TextUtils.TruncateAt.END
            }
            val nameParams = LinearLayout.LayoutParams(-1, -2)
            nameParams.topMargin = dp(8)
            item.addView(name, nameParams)
            val cell = GridLayout.LayoutParams().apply {
                width = 0
                height = dp(92)
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
            appGrid.addView(item, cell)
        }
        if (visible.isEmpty()) {
            val empty = label("No apps found", 13f, muted, false).apply { gravity = Gravity.CENTER }
            appGrid.addView(empty, GridLayout.LayoutParams().apply {
                width = -1
                height = dp(70)
                columnSpec = GridLayout.spec(0, 4)
            })
        }
    }

    private fun launchApp(info: ResolveInfo) {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        intent.setClassName(info.activityInfo.packageName, info.activityInfo.name)
        try { startActivity(intent) } catch (_: Exception) {}
    }

    private fun monochrome(drawable: Drawable): Drawable {
        val copy = drawable.constantState?.newDrawable()?.mutate() ?: drawable
        val matrix = ColorMatrix().apply { setSaturation(0f) }
        copy.colorFilter = ColorMatrixColorFilter(matrix)
        return copy
    }

    private fun label(text: String, size: Float, color: Int, bold: Boolean): TextView =
        TextView(this).apply {
            this.text = text
            textSize = size
            setTextColor(color)
            typeface = if (bold) Typeface.create("sans-serif-medium", Typeface.NORMAL)
                       else Typeface.create("sans-serif", Typeface.NORMAL)
        }

    private fun roundedBg(color: Int, radius: Int): android.graphics.drawable.GradientDrawable =
        android.graphics.drawable.GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius.toFloat()
        }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private class NothingBackdrop(context: android.content.Context) : View(context) {
        private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(35, 38, 42)
            style = Paint.Style.FILL
        }
        private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(24, 27, 31)
            style = Paint.Style.STROKE
            strokeWidth = 1.2f
        }
        private val redPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(232, 61, 79)
            style = Paint.Style.FILL
        }
        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val step = 18f * resources.displayMetrics.density
            var y = height * 0.32f
            while (y < height * 0.86f) {
                var x = width * 0.58f
                while (x < width - 8f) {
                    canvas.drawCircle(x, y, 1.05f, dotPaint)
                    x += step
                }
                y += step
            }
            val cx = width * 0.86f
            val cy = height * 0.19f
            canvas.drawCircle(cx, cy, 46f * resources.displayMetrics.density, ringPaint)
            canvas.drawCircle(cx, cy, 34f * resources.displayMetrics.density, ringPaint)
            canvas.drawCircle(cx + 31f, cy - 30f, 3.5f, redPaint)
        }
    }
}
