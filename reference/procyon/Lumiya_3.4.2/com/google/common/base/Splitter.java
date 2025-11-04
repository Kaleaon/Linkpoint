// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import java.util.LinkedHashMap;
import java.util.Map;
import com.google.common.annotations.Beta;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import com.google.common.annotations.GwtIncompatible;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.CheckReturnValue;
import java.util.Iterator;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public final class Splitter
{
    private final int limit;
    private final boolean omitEmptyStrings;
    private final Strategy strategy;
    private final CharMatcher trimmer;
    
    private Splitter(final Strategy strategy) {
        this(strategy, false, CharMatcher.NONE, Integer.MAX_VALUE);
    }
    
    private Splitter(final Strategy strategy, final boolean omitEmptyStrings, final CharMatcher trimmer, final int limit) {
        this.strategy = strategy;
        this.omitEmptyStrings = omitEmptyStrings;
        this.trimmer = trimmer;
        this.limit = limit;
    }
    
    @CheckReturnValue
    public static Splitter fixedLength(final int n) {
        boolean b = false;
        if (n > 0) {
            b = true;
        }
        Preconditions.checkArgument(b, (Object)"The length may not be less than 1");
        return new Splitter((Strategy)new Strategy() {
            public SplittingIterator iterator(final Splitter splitter, final CharSequence charSequence) {
                return new SplittingIterator(splitter, charSequence) {
                    public int separatorEnd(final int n) {
                        return n;
                    }
                    
                    public int separatorStart(int n) {
                        if ((n += n) >= this.toSplit.length()) {
                            n = -1;
                        }
                        return n;
                    }
                };
            }
        });
    }
    
    @CheckReturnValue
    public static Splitter on(final char c) {
        return on(CharMatcher.is(c));
    }
    
    @CheckReturnValue
    public static Splitter on(final CharMatcher charMatcher) {
        Preconditions.checkNotNull(charMatcher);
        return new Splitter((Strategy)new Strategy() {
            public SplittingIterator iterator(final Splitter splitter, final CharSequence charSequence) {
                return new SplittingIterator(splitter, charSequence) {
                    @Override
                    int separatorEnd(final int n) {
                        return n + 1;
                    }
                    
                    @Override
                    int separatorStart(final int n) {
                        return charMatcher.indexIn(this.toSplit, n);
                    }
                };
            }
        });
    }
    
    @CheckReturnValue
    public static Splitter on(final String s) {
        Preconditions.checkArgument(s.length() != 0, (Object)"The separator may not be the empty string.");
        if (s.length() != 1) {
            return new Splitter((Strategy)new Strategy() {
                public SplittingIterator iterator(final Splitter splitter, final CharSequence charSequence) {
                    return new SplittingIterator(splitter, charSequence) {
                        public int separatorEnd(final int n) {
                            return s.length() + n;
                        }
                        
                        public int separatorStart(int i) {
                            final int length = s.length();
                            final int length2 = this.toSplit.length();
                        Label_0021:
                            while (i <= length2 - length) {
                                for (int j = 0; j < length; ++j) {
                                    if (this.toSplit.charAt(j + i) != s.charAt(j)) {
                                        ++i;
                                        continue Label_0021;
                                    }
                                }
                                return i;
                            }
                            return -1;
                        }
                    };
                }
            });
        }
        return on(s.charAt(0));
    }
    
    @CheckReturnValue
    @GwtIncompatible("java.util.regex")
    public static Splitter on(final Pattern pattern) {
        Preconditions.checkNotNull(pattern);
        Preconditions.checkArgument(!pattern.matcher("").matches(), "The pattern may not match the empty string: %s", pattern);
        return new Splitter((Strategy)new Strategy() {
            public SplittingIterator iterator(final Splitter splitter, final CharSequence input) {
                return new SplittingIterator(splitter, input) {
                    final /* synthetic */ Matcher val$matcher = pattern.matcher(input);
                    
                    public int separatorEnd(final int n) {
                        return this.val$matcher.end();
                    }
                    
                    public int separatorStart(int start) {
                        if (!this.val$matcher.find(start)) {
                            start = -1;
                        }
                        else {
                            start = this.val$matcher.start();
                        }
                        return start;
                    }
                };
            }
        });
    }
    
    @CheckReturnValue
    @GwtIncompatible("java.util.regex")
    public static Splitter onPattern(final String regex) {
        return on(Pattern.compile(regex));
    }
    
    private Iterator<String> splittingIterator(final CharSequence charSequence) {
        return this.strategy.iterator(this, charSequence);
    }
    
    @CheckReturnValue
    public Splitter limit(final int i) {
        Preconditions.checkArgument(i > 0, "must be greater than zero: %s", i);
        return new Splitter(this.strategy, this.omitEmptyStrings, this.trimmer, i);
    }
    
    @CheckReturnValue
    public Splitter omitEmptyStrings() {
        return new Splitter(this.strategy, true, this.trimmer, this.limit);
    }
    
    @CheckReturnValue
    public Iterable<String> split(final CharSequence charSequence) {
        Preconditions.checkNotNull(charSequence);
        return new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return Splitter.this.splittingIterator(charSequence);
            }
            
            @Override
            public String toString() {
                return Joiner.on(", ").appendTo(new StringBuilder().append('['), (Iterable<?>)this).append(']').toString();
            }
        };
    }
    
    @CheckReturnValue
    @Beta
    public List<String> splitToList(final CharSequence charSequence) {
        Preconditions.checkNotNull(charSequence);
        final Iterator<String> splittingIterator = this.splittingIterator(charSequence);
        final ArrayList list = new ArrayList();
        while (splittingIterator.hasNext()) {
            list.add(splittingIterator.next());
        }
        return (List<String>)Collections.unmodifiableList((List<?>)list);
    }
    
    @CheckReturnValue
    public Splitter trimResults() {
        return this.trimResults(CharMatcher.WHITESPACE);
    }
    
    @CheckReturnValue
    public Splitter trimResults(final CharMatcher charMatcher) {
        Preconditions.checkNotNull(charMatcher);
        return new Splitter(this.strategy, this.omitEmptyStrings, charMatcher, this.limit);
    }
    
    @CheckReturnValue
    @Beta
    public MapSplitter withKeyValueSeparator(final char c) {
        return this.withKeyValueSeparator(on(c));
    }
    
    @CheckReturnValue
    @Beta
    public MapSplitter withKeyValueSeparator(final Splitter splitter) {
        return new MapSplitter(this, splitter);
    }
    
    @CheckReturnValue
    @Beta
    public MapSplitter withKeyValueSeparator(final String s) {
        return this.withKeyValueSeparator(on(s));
    }
    
    @Beta
    public static final class MapSplitter
    {
        private static final String INVALID_ENTRY_MESSAGE = "Chunk [%s] is not a valid entry";
        private final Splitter entrySplitter;
        private final Splitter outerSplitter;
        
        private MapSplitter(final Splitter outerSplitter, final Splitter splitter) {
            this.outerSplitter = outerSplitter;
            this.entrySplitter = Preconditions.checkNotNull(splitter);
        }
        
        @CheckReturnValue
        public Map<String, String> split(final CharSequence charSequence) {
            final LinkedHashMap m = new LinkedHashMap();
            for (final String s : this.outerSplitter.split(charSequence)) {
                final Iterator access$000 = this.entrySplitter.splittingIterator(s);
                Preconditions.checkArgument(access$000.hasNext(), "Chunk [%s] is not a valid entry", s);
                final String s2 = access$000.next();
                Preconditions.checkArgument(!m.containsKey(s2), "Duplicate key [%s] found.", s2);
                Preconditions.checkArgument(access$000.hasNext(), "Chunk [%s] is not a valid entry", s);
                m.put(s2, access$000.next());
                Preconditions.checkArgument(!access$000.hasNext(), "Chunk [%s] is not a valid entry", s);
            }
            return (Map<String, String>)Collections.unmodifiableMap((Map<?, ?>)m);
        }
    }
    
    private abstract static class SplittingIterator extends AbstractIterator<String>
    {
        int limit;
        int offset;
        final boolean omitEmptyStrings;
        final CharSequence toSplit;
        final CharMatcher trimmer;
        
        protected SplittingIterator(final Splitter splitter, final CharSequence toSplit) {
            this.offset = 0;
            this.trimmer = splitter.trimmer;
            this.omitEmptyStrings = splitter.omitEmptyStrings;
            this.limit = splitter.limit;
            this.toSplit = toSplit;
        }
        
        @Override
        protected String computeNext() {
            int n = this.offset;
            while (this.offset != -1) {
                int n2 = this.separatorStart(this.offset);
                if (n2 != -1) {
                    this.offset = this.separatorEnd(n2);
                }
                else {
                    n2 = this.toSplit.length();
                    this.offset = -1;
                }
                if (this.offset != n) {
                    while (n < n2 && this.trimmer.matches(this.toSplit.charAt(n))) {
                        ++n;
                    }
                    while (n2 > n && this.trimmer.matches(this.toSplit.charAt(n2 - 1))) {
                        --n2;
                    }
                    if (!this.omitEmptyStrings || n != n2) {
                        int n3;
                        if (this.limit != 1) {
                            --this.limit;
                            n3 = n2;
                        }
                        else {
                            int length = this.toSplit.length();
                            this.offset = -1;
                            while (true) {
                                n3 = length;
                                if (length <= n) {
                                    break;
                                }
                                n3 = length;
                                if (!this.trimmer.matches(this.toSplit.charAt(length - 1))) {
                                    break;
                                }
                                --length;
                            }
                        }
                        return this.toSplit.subSequence(n, n3).toString();
                    }
                    n = this.offset;
                }
                else {
                    ++this.offset;
                    if (this.offset < this.toSplit.length()) {
                        continue;
                    }
                    this.offset = -1;
                }
            }
            return this.endOfData();
        }
        
        abstract int separatorEnd(final int p0);
        
        abstract int separatorStart(final int p0);
    }
    
    private interface Strategy
    {
        Iterator<String> iterator(final Splitter p0, final CharSequence p1);
    }
}
