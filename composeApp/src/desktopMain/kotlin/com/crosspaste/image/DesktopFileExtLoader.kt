package com.crosspaste.image

import com.crosspaste.app.AppFileType
import com.crosspaste.os.linux.FreedesktopUtils.saveExtIcon
import com.crosspaste.os.macos.MacAppUtils
import com.crosspaste.os.windows.JIconExtract
import com.crosspaste.path.UserDataPathProvider
import com.crosspaste.platform.currentPlatform
import com.crosspaste.utils.ConcurrentPlatformMap
import com.crosspaste.utils.PlatformLock
import com.crosspaste.utils.createConcurrentPlatformMap
import com.crosspaste.utils.extension
import io.github.oshai.kotlinlogging.KotlinLogging
import okio.Path

class DesktopFileExtLoader(
    private val userDataPathProvider: UserDataPathProvider,
) : ConcurrentLoader<Path, Path>, FileExtImageLoader {

    private val logger = KotlinLogging.logger {}

    override val lockMap: ConcurrentPlatformMap<String, PlatformLock> = createConcurrentPlatformMap()

    private val platform = currentPlatform()

    private val toSave: (String, Path, Path) -> Unit =
        if (platform.isMacos()) {
            { key, _, result -> macSaveExtIcon(key, result) }
        } else if (platform.isWindows()) {
            { _, value, result -> windowsSaveExtIcon(value, result) }
        } else if (platform.isLinux()) {
            { key, _, result -> linuxSaveExtIcon(key, result) }
        } else {
            throw IllegalStateException("Unsupported platform: $platform")
        }

    override fun resolve(
        key: String,
        value: Path,
    ): Path {
        return userDataPathProvider.resolve("$key.png", AppFileType.FILE_EXT_ICON)
    }

    override fun exist(result: Path): Boolean {
        return result.toFile().exists()
    }

    override fun loggerWarning(
        value: Path,
        e: Exception,
    ) {
        logger.warn { "Failed to load icon for file extension: $value" }
    }

    override fun save(
        key: String,
        value: Path,
        result: Path,
    ) {
        toSave(key, value, result)
    }

    override fun convertToKey(value: Path): String {
        return value.extension
    }
}

private fun macSaveExtIcon(
    key: String,
    savePath: Path,
) {
    MacAppUtils.saveIconByExt(key, savePath.toString())
}

private fun windowsSaveExtIcon(
    filePath: Path,
    savePath: Path,
) {
    JIconExtract.getIconForFile(512, 512, filePath.toFile())?.let { icon ->
        ImageService.writeImage(icon, "png", savePath.toNioPath())
    }
}

private fun linuxSaveExtIcon(
    key: String,
    savePath: Path,
) {
    saveExtIcon(key, savePath)
}
