package com.crosspaste.paste.plugin

import com.crosspaste.dao.paste.PasteItem
import com.crosspaste.dao.paste.PasteType
import com.crosspaste.paste.PastePlugin
import com.crosspaste.paste.item.PasteFiles
import io.realm.kotlin.MutableRealm
import kotlin.io.path.isDirectory

object RemoveFolderImagePlugin : PastePlugin {
    override fun pluginProcess(
        pasteItems: List<PasteItem>,
        realm: MutableRealm,
    ): List<PasteItem> {
        pasteItems.firstOrNull { it.getPasteType() == PasteType.IMAGE }?.let { imageItem ->
            val files = imageItem as PasteFiles
            if (files.getFilePaths().size == 1) {
                pasteItems.firstOrNull { it.getPasteType() == PasteType.FILE }?.let {
                    val pasteFiles = it as PasteFiles
                    if (it.getFilePaths().size == 1 && pasteFiles.getFilePaths()[0].isDirectory()) {
                        imageItem.clear(realm, clearResource = true)
                        return pasteItems.filter { item -> item != imageItem }
                    }
                }
            }
        }
        return pasteItems
    }
}
