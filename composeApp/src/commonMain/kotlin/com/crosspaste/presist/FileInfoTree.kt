package com.crosspaste.presist

import com.crosspaste.paste.item.PasteFile
import com.crosspaste.paste.item.PasteFileImpl
import com.crosspaste.utils.getCodecsUtils
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import okio.Path

interface FileInfoTree {

    val size: Long

    val md5: String

    fun isFile(): Boolean

    fun getPasteFileList(path: Path): List<PasteFile>

    fun getCount(): Long
}

@Serializable
@SerialName("dir")
class DirFileInfoTree(
    private val tree: Map<String, FileInfoTree>,
    override val size: Long,
    override val md5: String,
) : FileInfoTree {
    @Transient
    private val sortTree: List<Pair<String, FileInfoTree>> =
        tree.entries.map { Pair(it.key, it.value) }
            .sortedBy { it.first }

    fun iterator(): Iterator<Pair<String, FileInfoTree>> {
        return sortTree.iterator()
    }

    override fun isFile(): Boolean {
        return false
    }

    override fun getPasteFileList(path: Path): List<PasteFile> {
        return tree.map { (name, fileTree) ->
            fileTree.getPasteFileList(path.resolve(name))
        }.flatten()
    }

    override fun getCount(): Long {
        return tree.values.sumOf { it.getCount() }
    }
}

@Serializable
@SerialName("file")
class SingleFileInfoTree(
    override val size: Long,
    override val md5: String,
) : FileInfoTree {

    override fun isFile(): Boolean {
        return true
    }

    override fun getPasteFileList(path: Path): List<PasteFile> {
        return listOf(PasteFileImpl(path, md5))
    }

    override fun getCount(): Long {
        return 1L
    }
}

class FileInfoTreeBuilder {

    private val codecsUtils = getCodecsUtils()

    private val tree = mutableMapOf<String, FileInfoTree>()

    private var size = 0L

    private val md5List = mutableListOf<String>()

    fun addFileInfoTree(
        name: String,
        fileInfoTree: FileInfoTree,
    ) {
        tree[name] = fileInfoTree
        size += fileInfoTree.size
        md5List.add(fileInfoTree.md5)
    }

    fun build(path: Path): FileInfoTree {
        val md5 =
            if (md5List.isEmpty()) {
                codecsUtils.md5ByString(path.name)
            } else {
                codecsUtils.md5ByArray(md5List.toTypedArray())
            }
        return DirFileInfoTree(tree, size, md5)
    }
}
