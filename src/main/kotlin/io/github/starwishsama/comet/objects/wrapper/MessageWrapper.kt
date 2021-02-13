package io.github.starwishsama.comet.objects.wrapper

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageChainBuilder
import kotlin.streams.toList

open class MessageWrapper: Cloneable {
    private val messageContent = mutableSetOf<WrapperElement>()
    @Volatile
    private var usable: Boolean = true

    fun addElement(element: WrapperElement): MessageWrapper {
        messageContent.add(element)
        return this
    }

    fun addElements(vararg element: WrapperElement): MessageWrapper {
        messageContent.addAll(element)
        return this
    }

    fun addElements(elements: Collection<WrapperElement>): MessageWrapper {
        messageContent.addAll(elements)
        return this
    }

    fun addText(text: String): MessageWrapper {
        messageContent.add(PureText(text))
        return this
    }

    fun addPictureByURL(url: String?): MessageWrapper {
        if (url == null) return this

        messageContent.add(Picture(url))
        return this
    }

    fun setUsable(usable: Boolean): MessageWrapper {
        this.usable = usable
        return this
    }

    fun isUsable(): Boolean {
        return usable
    }

    fun toMessageChain(subject: Contact): MessageChain {
        return MessageChainBuilder().apply {
            messageContent.forEach {
                if (it !is Picture || !isPictureReachLimit()) {
                    add(it.toMessageContent(subject))
                }
            }
        }.build()
    }

    private fun isPictureReachLimit(): Boolean {
        return messageContent.parallelStream().filter { it is Picture }.count() >= 9
    }

    override fun toString(): String {
        return "MessageWrapper {content=${messageContent}}"
    }

    fun getAllText(): String {
        val texts = messageContent.parallelStream().filter { it is PureText }.toList()
        return buildString {
            texts.forEach {
                append(it.asString())
            }
        }
    }

    fun getMessageContent(): Set<WrapperElement> {
        return mutableSetOf<WrapperElement>().apply {
            addAll(messageContent)
        }
    }

    fun compare(other: Any?): Boolean {
        if (other !is MessageWrapper) return false

        return getMessageContent() == other.getMessageContent()
    }
}