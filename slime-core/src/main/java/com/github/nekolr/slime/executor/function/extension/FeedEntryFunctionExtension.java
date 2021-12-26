package com.github.nekolr.slime.executor.function.extension;

import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExtension;
import com.github.nekolr.slime.util.FeedUtils;
import com.rometools.rome.feed.synd.SyndEntry;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class FeedEntryFunctionExtension implements FunctionExtension {

    @Override
    public Class<?> support() {
        return SyndEntry.class;
    }

    @Comment("获取实体的作者")
    @Example("${entryVar.author()}")
    public static String author(SyndEntry entry) {
        return FeedUtils.getAuthor(entry);
    }

    @Comment("获取实体的链接")
    @Example("${entryVar.link()}")
    public static String link(SyndEntry entry) {
        return FeedUtils.getLink(entry);
    }

    @Comment("获取实体的标题")
    @Example("${entryVar.title()}")
    public static String title(SyndEntry entry) {
        return FeedUtils.getTitle(entry);
    }

    @Comment("获取实体的 uri（guid）")
    @Example("${entryVar.uri()}")
    public static String uri(SyndEntry entry) {
        return FeedUtils.getUri(entry);
    }

    @Comment("获取实体的描述")
    @Example("${entryVar.desc()}")
    public static String desc(SyndEntry entry) {
        return FeedUtils.getDescription(entry);
    }

    @Comment("获取实体的发布时间")
    @Example("${entryVar.pubDate()}")
    public static Date pubDate(SyndEntry entry) {
        return FeedUtils.getPublishedDate(entry);
    }
}
