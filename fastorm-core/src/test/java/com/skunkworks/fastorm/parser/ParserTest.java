package com.skunkworks.fastorm.parser;

import com.skunkworks.fastorm.processor.cache.CacheQueryListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;
import org.skunkworks.fastorm.parser.CacheQueryBaseListener;
import org.skunkworks.fastorm.parser.CacheQueryLexer;
import org.skunkworks.fastorm.parser.CacheQueryParser;
import org.skunkworks.fastorm.parser.QueryBaseListener;
import org.skunkworks.fastorm.parser.QueryLexer;
import org.skunkworks.fastorm.parser.QueryParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * stole on 04.02.17.
 */
public class ParserTest {
    private static final Logger l = Logger.getLogger(ParserTest.class.getName());

    @Test
    public void regex() throws Exception {
        //a?a?a?aaa
        //Pattern pattern = Pattern.compile("\\w+");
        Pattern pattern = Pattern.compile("a?a?a?a?a?a?a?a?a?a?a?a?a?a?a?a?a?a?a?a?a?a?a?a?a?a?a?a?a?a?aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        long start = System.nanoTime();
        Matcher matcher = pattern.matcher("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        boolean matches = matcher.matches();

        l.info("matches:" + matches + " in " + (System.nanoTime() - start));
    }

    @Test
    public void parse() throws Exception {
        //String sql = "findByNameOrLastNameOrderByBanana";
        String sql = "findByNameOrLastNameAndLastName1OrLastName2OrderByBanana";
//        String sql = "findByNameAndLastName";
        final InputStream is = new ByteArrayInputStream(sql.getBytes(Charset.forName("UTF-8")));

        final CharStream inputStream = CharStreams.fromStream(is);
        // Create an ExprLexer that feeds from that stream
        final QueryLexer lexer = new QueryLexer(inputStream);
        // Create a stream of tokens fed by the lexer
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        // Create a parser that feeds off the token stream
        final QueryParser parser = new QueryParser(tokens);
        // Begin parsing at rule query
//        final QueryParser.QueryContext queryContext = parser.query();
//        Query ctx = queryContext.ctx;

        ParseTree tree = parser.query();
        ParseTreeWalker walker = new ParseTreeWalker();
        QueryListener listener = new QueryListener();
        walker.walk(listener, tree);

        l.info("Syntax Errors:" + parser.getNumberOfSyntaxErrors());
//        l.info("Done:" + queryContext.getText());
    }

    class QueryListener extends QueryBaseListener {
        @Override
        public void exitExpression(QueryParser.ExpressionContext ctx) {
            l.info(ctx.getText());
        }

        @Override
        public void enterQuery(QueryParser.QueryContext ctx) {
            l.info("enterQuery:" + ctx.getText());
        }

        @Override
        public void exitQuery(QueryParser.QueryContext ctx) {
            l.info("exitQuery:" + ctx.getText());
        }
    }

    @Test
    public void parseCache() throws Exception {
        String cacheQuery = "findByNameLastNameAndLastName1AndLastName2";
        final InputStream is = new ByteArrayInputStream(cacheQuery.getBytes(Charset.forName("UTF-8")));

        final CharStream inputStream = CharStreams.fromStream(is);
        // Create an ExprLexer that feeds from that stream
        final CacheQueryLexer lexer = new CacheQueryLexer(inputStream);
        // Create a stream of tokens fed by the lexer
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        // Create a parser that feeds off the token stream
        final CacheQueryParser parser = new CacheQueryParser(tokens);
        // Begin parsing at rule query
//        final QueryParser.QueryContext queryContext = parser.query();
//        Query ctx = queryContext.ctx;

        ParseTree tree = parser.query();
        ParseTreeWalker walker = new ParseTreeWalker();
        CacheQueryListener listener = new CacheQueryListener();
        walker.walk(listener, tree);

        l.info("Syntax Errors:" + parser.getNumberOfSyntaxErrors());
//        l.info("Done:" + queryContext.getText());
    }
}
