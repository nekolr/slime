package com.github.nekolr.slime.util;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import java.io.StringReader;
import java.util.*;

@Slf4j
public class FeedUtils {

    public static SyndFeed getFeed(String xml) {
        Document document;
        SyndFeed feed = null;
        SAXBuilder saxBuilder = new SAXBuilder();
        StringReader reader = new StringReader(xml);
        SyndFeedInput feedInput = new SyndFeedInput();
        try {
            document = saxBuilder.build(reader);
            feed = feedInput.build(document);
        } catch (Exception e) {
            log.error("解析 feed 失败", e);
        } finally {
            reader.close();
        }
        return feed;
    }

    public static String getAuthor(SyndFeed feed) {
        if (Objects.nonNull(feed)) {
            return feed.getAuthor();
        }
        return null;
    }

    public static String getTitle(SyndFeed feed) {
        if (Objects.nonNull(feed)) {
            return feed.getTitle();
        }
        return null;
    }

    public static String getGenerator(SyndFeed feed) {
        if (Objects.nonNull(feed)) {
            return feed.getGenerator();
        }
        return null;
    }

    public static String getLink(SyndFeed feed) {
        if (Objects.nonNull(feed)) {
            return feed.getLink();
        }
        return null;
    }

    public static String getDescription(SyndFeed feed) {
        if (Objects.nonNull(feed)) {
            return feed.getDescription();
        }
        return null;
    }

    public static String getWebMaster(SyndFeed feed) {
        if (Objects.nonNull(feed)) {
            return feed.getWebMaster();
        }
        return null;
    }

    public static List<SyndEntry> getEntries(SyndFeed feed) {
        if (Objects.nonNull(feed)) {
            return feed.getEntries();
        }
        return Collections.emptyList();
    }

    public static Optional<SyndEntry> getEntry(SyndFeed feed, int index) {
        if (Objects.nonNull(feed)) {
            List<SyndEntry> entries = feed.getEntries();
            if (!entries.isEmpty()) {
                return Optional.ofNullable(entries.get(index));
            }
        }
        return Optional.empty();
    }

    public static String getAuthor(SyndEntry entry) {
        if (Objects.nonNull(entry)) {
            return entry.getAuthor();
        }
        return null;
    }

    public static String getLink(SyndEntry entry) {
        if (Objects.nonNull(entry)) {
            return entry.getLink();
        }
        return null;
    }

    public static String getTitle(SyndEntry entry) {
        if (Objects.nonNull(entry)) {
            return entry.getTitle();
        }
        return null;
    }

    public static String getUri(SyndEntry entry) {
        if (Objects.nonNull(entry)) {
            return entry.getUri();
        }
        return null;
    }

    public static String getDescription(SyndEntry entry) {
        if (Objects.nonNull(entry)) {
            SyndContent content = entry.getDescription();
            if (Objects.nonNull(content)) {
                return content.getValue();
            }
        }
        return null;
    }

    public static Date getPublishedDate(SyndEntry entry) {
        if (Objects.nonNull(entry)) {
            return entry.getPublishedDate();
        }
        return null;
    }

    public static SyndContent getDescriptionEx(SyndEntry entry) {
        if (Objects.nonNull(entry)) {
            return entry.getDescription();
        }
        return null;
    }

    public static String getContentType(SyndContent content) {
        if (Objects.nonNull(content)) {
            return content.getType();
        }
        return null;
    }

    public static String getContentValue(SyndContent content) {
        if (Objects.nonNull(content)) {
            return content.getValue();
        }
        return null;
    }
}
