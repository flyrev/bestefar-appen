package flyrev.bestefar

import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_bestefar.*


class Bestefar : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bestefar)

        if (canWriteToSystemSettings) {
            setMaxBrightness()
        } else {
            openAllowWritePermissionScreen()
        }

        val contentResolver = contentResolver // ?
        val setting = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS)
        val observer = object : ContentObserver(Handler()) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)

                if (!canWriteToSystemSettings) {
                    text.text = "Du må gi applikasjonen tillatelse til å endre lysstyrken. Lukk og åpne appen på nytt, deretter gi applikasjonen tilgang."
                    return
                }

                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
                setMaxBrightness()
            }

            override fun deliverSelfNotifications(): Boolean {
                return true
            }
        }
        contentResolver.registerContentObserver(setting, false, observer)
    }

}

fun Context.setMaxBrightness() {
    setBrightness(255)
}

fun Context.setBrightness(value: Int) {
    Settings.System.putInt(
            this.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            value
    )
}

fun Context.openAllowWritePermissionScreen() {
    val intent = Intent(
            Settings.ACTION_MANAGE_WRITE_SETTINGS,
            Uri.parse("package:$packageName")
    )
    startActivity(intent)
}

val Context.canWriteToSystemSettings: Boolean
    get() {
        return Settings.System.canWrite(this)
    }