package org.serverct.parrot.parrotx.update

import com.google.gson.JsonParser
import org.serverct.parrot.parrotx.function.sendInfo
import taboolib.common.platform.function.pluginId
import taboolib.common.platform.function.pluginVersion
import taboolib.common.platform.function.submitAsync
import java.net.URL
import java.util.function.Consumer

@Suppress("unused", "MemberVisibilityCanBePrivate")
/**
 * 基于 Github API 检测 release 的更新检查器
 * 使用方法：
 * ```kotlin
 * GithubUpdateChecker("l1-an", "YuSpawnerHologram").getVersion {
 *     if (pluginVersion == it) {
 *         sendInfo(&f[ $pluginId &f] There is not a new update available.)
 *     } else {
 *         sendInfo {
 *             +"&f[ $pluginId &f] There is a new update available at:"
 *             +"https://github.com/L1-An/YuSpawnerHologram/releases/latest"
 *         }
 *     }
 * }
 * ```
 * 或者
 * ```kotlin
 * GithubUpdateChecker("l1-an", "YuSpawnerHologram").check()
 * ```
 */
class GithubUpdateChecker(private val author: String, private val githubRepository: String) {

    fun getVersion(consumer: Consumer<String>) {
        submitAsync {
            runCatching {
                val jsonStr = URL("https://api.github.com/repos/$author/$githubRepository/releases/latest").readText()
                val jsonObj = JsonParser.parseString(jsonStr).asJsonObject
                val version = jsonObj["tag_name"].asString

                consumer.accept(version)
            }.onFailure {
                sendInfo("&f[ $pluginId &f] Unable to check for update. This may be a problem caused by the network or the Github repository is not exists.")
            }
        }
    }

    fun check() {
        getVersion { version ->
            if (pluginVersion == version) {
                sendInfo("&f[ $pluginId &f] There is not a new update available.")
            } else {
                UpdateManager.isUpdateAvailable = true
                UpdateManager.latestVersion = version
                UpdateManager.link = "https://www.github.com/$author/$githubRepository/releases/latest"
                sendInfo {
                    +"&f[ $pluginId &f] There is a new update available at:"
                    +"https://www.github.com/$author/$githubRepository/releases/latest"
                }
            }
        }
    }

}