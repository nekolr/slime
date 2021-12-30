package com.github.nekolr.executor.function.extension;

import com.github.nekolr.model.WebElements;
import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebElementWrapperFunctionExtension implements FunctionExtension {

    @Override
    public Class<?> support() {
        return WebElementWrapper.class;
    }

    @Comment("根据 css 选择器提取请求结果")
    @Example("${elementVar.selector('div > a')}")
    public static WebElementWrapper selector(WebElementWrapper element, String css) {
        try {
            return (WebElementWrapper) element.findElement(By.cssSelector(css));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Comment("根据 css 选择器提取请求结果")
    @Example("${elementVar.selector('div > a')}")
    public static WebElements selectors(WebElementWrapper element, String css) {
        try {
            return new WebElements(element.getResponse(), element.findElements(By.cssSelector(css)));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Comment("根据 xpath 在请求结果中查找")
    @Example("${elementVar.xpaths('//a')}")
    public static List<WebElement> xpaths(WebElementWrapper wrapper, String xpath) {
        try {
            return wrapper.findElements(By.xpath(xpath));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Comment("根据 xpath 在请求结果中查找")
    @Example("${elementVar.xpath('//a')}")
    public static WebElement xpath(WebElementWrapper wrapper, String xpath) {
        try {
            return wrapper.findElement(By.xpath(xpath));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Comment("点击并且不释放")
    @Example("${elementVar.clickAndHold()}")
    public static WebElementWrapper clickAndHold(WebElementWrapper element) {
        element.action().clickAndHold(element.element());
        return element;
    }

    @Comment("释放")
    @Example("${elementVar.release()}")
    public static WebElementWrapper release(WebElementWrapper element) {
        element.action().release(element.element());
        return element;
    }

    @Comment("移动鼠标")
    @Example("${elementVar.move(15,0)}")
    public static WebElementWrapper move(WebElementWrapper element, int x, int y) {
        element.action().moveToElement(element.element(), x, y);
        return element;
    }

    @Comment("移动鼠标到该节点上")
    @Example("${elementVar.move()}")
    public static WebElementWrapper move(WebElementWrapper element) {
        element.action().moveToElement(element.element());
        return element;
    }

    @Comment("双击节点")
    @Example("${elementVar.doubleClick()}")
    public static WebElementWrapper doubleClick(WebElementWrapper element) {
        element.action().doubleClick(element.element());
        return element;
    }

    @Comment("暂缓")
    @Example("${elementVar.pause(500)}")
    public static WebElementWrapper pause(WebElementWrapper element, int pause) {
        element.action().pause(pause);
        return element;
    }

    @Comment("执行动作")
    @Example("${elementVar.perform()}")
    public static WebElementWrapper perform(WebElementWrapper element) {
        element.action().perform();
        element.clear();
        return element;
    }
}
