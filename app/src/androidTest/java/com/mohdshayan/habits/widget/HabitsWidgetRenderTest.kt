package com.mohdshayan.habits.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RemoteViews
import android.widget.TextView
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.ExperimentalGlanceRemoteViewsApi
import androidx.glance.appwidget.GlanceRemoteViews
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Applies the widget's RemoteViews the way a launcher does. A colour or layout that the launcher
 * cannot resolve throws here, which is what showed as "Can't load widget" in 1.3 and 1.3.1.
 */
@RunWith(AndroidJUnit4::class)
class HabitsWidgetRenderTest {
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    private fun applyOnMain(views: RemoteViews): View {
        lateinit var root: View
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            root = views.apply(context, FrameLayout(context))
        }
        return root
    }

    private fun texts(view: View): List<String> = when (view) {
        is TextView -> listOf(view.text.toString())
        is ViewGroup -> (0 until view.childCount).flatMap { texts(view.getChildAt(it)) }
        else -> emptyList()
    }

    @OptIn(ExperimentalGlanceRemoteViewsApi::class)
    @Test
    fun widgetContentAppliesOnTheLauncherSide() {
        val views = runBlocking {
            GlanceRemoteViews().compose(context, DpSize(250.dp, 110.dp)) {
                WidgetContent(done = 2, total = 3)
            }.remoteViews
        }
        val shown = texts(applyOnMain(views))
        assertTrue(shown.toString(), shown.any { it.contains("2") })
        assertTrue(shown.toString(), shown.any { it.contains("3") })
    }

    @Test
    fun providerHasLoadingLayoutAndPreview() {
        val info = AppWidgetManager.getInstance(context)
            .getInstalledProvidersForPackage(context.packageName, null)
            .single { it.provider.className == HabitsWidgetReceiver::class.java.name }
        assertNotEquals("initialLayout", 0, info.initialLayout)
        applyOnMain(RemoteViews(context.packageName, info.initialLayout))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            assertNotEquals("previewLayout", 0, info.previewLayout)
            applyOnMain(RemoteViews(context.packageName, info.previewLayout))
            assertTrue("targetCellWidth", info.targetCellWidth > 0)
            assertTrue("targetCellHeight", info.targetCellHeight > 0)
        }
    }
}
