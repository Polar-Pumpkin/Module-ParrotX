package org.serverct.parrot.parrotx.update

import org.serverct.parrot.parrotx.function.sendInfo
import taboolib.common.platform.function.pluginId
import taboolib.common.platform.function.pluginVersion
import taboolib.common.platform.function.submitAsync
import java.net.URL
import java.util.*
import java.util.function.Consumer

@Suppress("unused", "MemberVisibilityCanBePrivate")
/**
 * 基于 SpigotMC API 检测版本号的更新检查器
 * 使用方法：
 * ```kotlin
 * SpigotUpdateChecker(12345).getVersion {
 *     if (pluginVersion == it) {
 *         sendInfo(&f[ $pluginId &f] There is not a new update available.)
 *     } else {
 *         sendInfo {
 *             +"&f[ $pluginId &f] There is a new update available at:"
 *             +"https://www.spigotmc.org/resources/12345/"
 *         }
 *     }
 * }
 * ```
 * 或者
 * ```kotlin
 * SpigotUpdateChecker(12345).check()
 * ```
 */
class SpigotUpdateChecker(private val resourceId: Int) {

    fun getVersion(consumer: Consumer<String?>) {
        submitAsync {
            runCatching {
                URL("https://api.spigotmc.org/legacy/update.php?resource=$resourceId/~")
                    .openStream().use { stream ->
                        Scanner(stream).use { scanner ->
                            if (scanner.hasNext()) {
                                consumer.accept(scanner.next())
                            }
                        }
                    }
            }.onFailure {
                sendInfo("&f[ $pluginId &f] Unable to check for update. This may be a problem caused by the network.")
            }
        }
    }

    fun check() {
        getVersion { version ->
            if (version == null) {
                sendInfo("&f[ $pluginId &f] Unable to check for update. This may be a problem caused by the network.")
            } else if (pluginVersion == version) {
                sendInfo("&f[ $pluginId &f] There is not a new update available.")
            } else {
                UpdateManager.isUpdateAvailable = true
                UpdateManager.latestVersion = version
                UpdateManager.link = "https://www.spigotmc.org/resources/$resourceId/"
                sendInfo {
                    +"&f[ $pluginId &f] There is a new update available at:"
                    +"https://www.spigotmc.org/resources/$resourceId/"
                }
            }
        }
    }
}