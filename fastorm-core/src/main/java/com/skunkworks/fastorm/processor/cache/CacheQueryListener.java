package com.skunkworks.fastorm.processor.cache;

import org.skunkworks.fastorm.parser.CacheQueryBaseListener;
import org.skunkworks.fastorm.parser.CacheQueryParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * stole on 11.06.17.
 */
public class CacheQueryListener extends CacheQueryBaseListener {

    private final List<String> keyComponents = new ArrayList<>();

    @Override
    public void enterQuery(CacheQueryParser.QueryContext ctx) {
        //l.info("enterQuery:" + ctx.getText());
    }

    @Override
    public void exitQuery(CacheQueryParser.QueryContext ctx) {
        //l.info("exitQuery:" + ctx.getText());
    }

    @Override
    public void exitAtom(CacheQueryParser.AtomContext ctx) {
        keyComponents.add(ctx.getText());
    }

    public List<String> getKeyComponents() {
        return Collections.unmodifiableList(keyComponents);
    }
}
