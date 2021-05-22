package com.github.nekolr.slime.executor.function.extension;

import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExtension;
import com.github.nekolr.slime.util.FeedUtils;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class FeedFunctionExtension implements FunctionExtension {

    @Override
    public Class<?> support() {
        return SyndFeed.class;
    }

    @Comment("获取作者")
    @Example("${feedVar.author()}")
    public static String author(SyndFeed feed) {
        return FeedUtils.getAuthor(feed);
    }

    @Comment("获取标题")
    @Example("${feedVar.title()}")
    public static String title(SyndFeed feed) {
        return FeedUtils.getTitle(feed);
    }

    @Comment("获取生成器")
    @Example("${feedVar.generator()}")
    public static String generator(SyndFeed feed) {
        return FeedUtils.getGenerator(feed);
    }

    @Comment("获取链接")
    @Example("${feedVar.link()}")
    public static String link(SyndFeed feed) {
        return FeedUtils.getLink(feed);
    }

    @Comment("获取站长")
    @Example("${feedVar.webMaster()}")
    public static String webMaster(SyndFeed feed) {
        return FeedUtils.getWebMaster(feed);
    }

    @Comment("获取描述")
    @Example("${feedVar.desc()}")
    public static String desc(SyndFeed feed) {
        return FeedUtils.getDescription(feed);
    }

    @Comment("获取实体集合")
    @Example("${feedVar.entries()}")
    public static List<SyndEntry> entries(SyndFeed feed) {
        return FeedUtils.getEntries(feed);
    }

    @Comment("获取某个实体")
    @Example("${feedVar.entries(0)}")
    public static SyndEntry entries(SyndFeed feed, int index) {
        return FeedUtils.getEntry(feed, index).orElse(null);
    }

    @Comment("获取实体的作者")
    @Example("${feedVar.author(entry)}")
    public static String author(SyndFeed feed, SyndEntry entry) {
        return FeedUtils.getAuthor(entry);
    }

    @Comment("获取实体的链接")
    @Example("${feedVar.link(entry)}")
    public static String link(SyndFeed feed, SyndEntry entry) {
        return FeedUtils.getLink(entry);
    }

    @Comment("获取实体的标题")
    @Example("${feedVar.title(entry)}")
    public static String title(SyndFeed feed, SyndEntry entry) {
        return FeedUtils.getTitle(entry);
    }

    @Comment("获取实体的 uri（guid）")
    @Example("${feedVar.uri(entry)}")
    public static String uri(SyndFeed feed, SyndEntry entry) {
        return FeedUtils.getUri(entry);
    }

    @Comment("获取实体的描述")
    @Example("${feedVar.desc(entry)}")
    public static String desc(SyndFeed feed, SyndEntry entry) {
        return FeedUtils.getDescription(entry);
    }

    @Comment("获取实体的发布时间")
    @Example("${feedVar.pubDate(entry)}")
    public static Date pubDate(SyndFeed feed, SyndEntry entry) {
        return FeedUtils.getPublishedDate(entry);
    }

}
