package com.google.common.cache;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.cache.LocalCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public final class CacheBuilderSpec {
    private static final Splitter KEYS_SPLITTER = Splitter.on(',').trimResults();
    private static final Splitter KEY_VALUE_SPLITTER = Splitter.on('=').trimResults();
    private static final ImmutableMap<String, ValueParser> VALUE_PARSERS = ImmutableMap.builder().put("initialCapacity", new InitialCapacityParser()).put("maximumSize", new MaximumSizeParser()).put("maximumWeight", new MaximumWeightParser()).put("concurrencyLevel", new ConcurrencyLevelParser()).put("weakKeys", new KeyStrengthParser(LocalCache.Strength.WEAK)).put("softValues", new ValueStrengthParser(LocalCache.Strength.SOFT)).put("weakValues", new ValueStrengthParser(LocalCache.Strength.WEAK)).put("recordStats", new RecordStatsParser()).put("expireAfterAccess", new AccessDurationParser()).put("expireAfterWrite", new WriteDurationParser()).put("refreshAfterWrite", new RefreshDurationParser()).put("refreshInterval", new RefreshDurationParser()).build();
    @VisibleForTesting
    long accessExpirationDuration;
    @VisibleForTesting
    TimeUnit accessExpirationTimeUnit;
    @VisibleForTesting
    Integer concurrencyLevel;
    @VisibleForTesting
    Integer initialCapacity;
    @VisibleForTesting
    LocalCache.Strength keyStrength;
    @VisibleForTesting
    Long maximumSize;
    @VisibleForTesting
    Long maximumWeight;
    @VisibleForTesting
    Boolean recordStats;
    @VisibleForTesting
    long refreshDuration;
    @VisibleForTesting
    TimeUnit refreshTimeUnit;
    private final String specification;
    @VisibleForTesting
    LocalCache.Strength valueStrength;
    @VisibleForTesting
    long writeExpirationDuration;
    @VisibleForTesting
    TimeUnit writeExpirationTimeUnit;

    /* loaded from: classes.dex */
    static class AccessDurationParser extends DurationParser {
        AccessDurationParser() {
        }

        @Override // com.google.common.cache.CacheBuilderSpec.DurationParser
        protected void parseDuration(CacheBuilderSpec cacheBuilderSpec, long j, TimeUnit timeUnit) {
            Preconditions.checkArgument(cacheBuilderSpec.accessExpirationTimeUnit == null, "expireAfterAccess already set");
            cacheBuilderSpec.accessExpirationDuration = j;
            cacheBuilderSpec.accessExpirationTimeUnit = timeUnit;
        }
    }

    /* loaded from: classes.dex */
    static class ConcurrencyLevelParser extends IntegerParser {
        ConcurrencyLevelParser() {
        }

        @Override // com.google.common.cache.CacheBuilderSpec.IntegerParser
        protected void parseInteger(CacheBuilderSpec cacheBuilderSpec, int i) {
            Preconditions.checkArgument(cacheBuilderSpec.concurrencyLevel == null, "concurrency level was already set to ", cacheBuilderSpec.concurrencyLevel);
            cacheBuilderSpec.concurrencyLevel = Integer.valueOf(i);
        }
    }

    /* loaded from: classes.dex */
    static abstract class DurationParser implements ValueParser {
        DurationParser() {
        }

        @Override // com.google.common.cache.CacheBuilderSpec.ValueParser
        public void parse(CacheBuilderSpec cacheBuilderSpec, String str, String str2) {
            TimeUnit timeUnit;
            Preconditions.checkArgument((str2 == null || str2.isEmpty()) ? false : true, "value of key %s omitted", str);
            try {
                switch (str2.charAt(str2.length() - 1)) {
                    case 'd':
                        timeUnit = TimeUnit.DAYS;
                        break;
                    case 'h':
                        timeUnit = TimeUnit.HOURS;
                        break;
                    case 'm':
                        timeUnit = TimeUnit.MINUTES;
                        break;
                    case 's':
                        timeUnit = TimeUnit.SECONDS;
                        break;
                    default:
                        throw new IllegalArgumentException(CacheBuilderSpec.format("key %s invalid format.  was %s, must end with one of [dDhHmMsS]", str, str2));
                }
                parseDuration(cacheBuilderSpec, Long.parseLong(str2.substring(0, str2.length() - 1)), timeUnit);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(CacheBuilderSpec.format("key %s value set to %s, must be integer", str, str2));
            }
        }

        protected abstract void parseDuration(CacheBuilderSpec cacheBuilderSpec, long j, TimeUnit timeUnit);
    }

    /* loaded from: classes.dex */
    static class InitialCapacityParser extends IntegerParser {
        InitialCapacityParser() {
        }

        @Override // com.google.common.cache.CacheBuilderSpec.IntegerParser
        protected void parseInteger(CacheBuilderSpec cacheBuilderSpec, int i) {
            Preconditions.checkArgument(cacheBuilderSpec.initialCapacity == null, "initial capacity was already set to ", cacheBuilderSpec.initialCapacity);
            cacheBuilderSpec.initialCapacity = Integer.valueOf(i);
        }
    }

    /* loaded from: classes.dex */
    static abstract class IntegerParser implements ValueParser {
        IntegerParser() {
        }

        @Override // com.google.common.cache.CacheBuilderSpec.ValueParser
        public void parse(CacheBuilderSpec cacheBuilderSpec, String str, String str2) {
            Preconditions.checkArgument((str2 == null || str2.isEmpty()) ? false : true, "value of key %s omitted", str);
            try {
                parseInteger(cacheBuilderSpec, Integer.parseInt(str2));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(CacheBuilderSpec.format("key %s value set to %s, must be integer", str, str2), e);
            }
        }

        protected abstract void parseInteger(CacheBuilderSpec cacheBuilderSpec, int i);
    }

    /* loaded from: classes.dex */
    static class KeyStrengthParser implements ValueParser {
        private final LocalCache.Strength strength;

        public KeyStrengthParser(LocalCache.Strength strength) {
            this.strength = strength;
        }

        @Override // com.google.common.cache.CacheBuilderSpec.ValueParser
        public void parse(CacheBuilderSpec cacheBuilderSpec, String str, @Nullable String str2) {
            Preconditions.checkArgument(str2 == null, "key %s does not take values", str);
            Preconditions.checkArgument(cacheBuilderSpec.keyStrength == null, "%s was already set to %s", str, cacheBuilderSpec.keyStrength);
            cacheBuilderSpec.keyStrength = this.strength;
        }
    }

    /* loaded from: classes.dex */
    static abstract class LongParser implements ValueParser {
        LongParser() {
        }

        @Override // com.google.common.cache.CacheBuilderSpec.ValueParser
        public void parse(CacheBuilderSpec cacheBuilderSpec, String str, String str2) {
            Preconditions.checkArgument((str2 == null || str2.isEmpty()) ? false : true, "value of key %s omitted", str);
            try {
                parseLong(cacheBuilderSpec, Long.parseLong(str2));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(CacheBuilderSpec.format("key %s value set to %s, must be integer", str, str2), e);
            }
        }

        protected abstract void parseLong(CacheBuilderSpec cacheBuilderSpec, long j);
    }

    /* loaded from: classes.dex */
    static class MaximumSizeParser extends LongParser {
        MaximumSizeParser() {
        }

        @Override // com.google.common.cache.CacheBuilderSpec.LongParser
        protected void parseLong(CacheBuilderSpec cacheBuilderSpec, long j) {
            Preconditions.checkArgument(cacheBuilderSpec.maximumSize == null, "maximum size was already set to ", cacheBuilderSpec.maximumSize);
            Preconditions.checkArgument(cacheBuilderSpec.maximumWeight == null, "maximum weight was already set to ", cacheBuilderSpec.maximumWeight);
            cacheBuilderSpec.maximumSize = Long.valueOf(j);
        }
    }

    /* loaded from: classes.dex */
    static class MaximumWeightParser extends LongParser {
        MaximumWeightParser() {
        }

        @Override // com.google.common.cache.CacheBuilderSpec.LongParser
        protected void parseLong(CacheBuilderSpec cacheBuilderSpec, long j) {
            Preconditions.checkArgument(cacheBuilderSpec.maximumWeight == null, "maximum weight was already set to ", cacheBuilderSpec.maximumWeight);
            Preconditions.checkArgument(cacheBuilderSpec.maximumSize == null, "maximum size was already set to ", cacheBuilderSpec.maximumSize);
            cacheBuilderSpec.maximumWeight = Long.valueOf(j);
        }
    }

    /* loaded from: classes.dex */
    static class RecordStatsParser implements ValueParser {
        RecordStatsParser() {
        }

        @Override // com.google.common.cache.CacheBuilderSpec.ValueParser
        public void parse(CacheBuilderSpec cacheBuilderSpec, String str, @Nullable String str2) {
            Preconditions.checkArgument(str2 == null, "recordStats does not take values");
            Preconditions.checkArgument(cacheBuilderSpec.recordStats == null, "recordStats already set");
            cacheBuilderSpec.recordStats = true;
        }
    }

    /* loaded from: classes.dex */
    static class RefreshDurationParser extends DurationParser {
        RefreshDurationParser() {
        }

        @Override // com.google.common.cache.CacheBuilderSpec.DurationParser
        protected void parseDuration(CacheBuilderSpec cacheBuilderSpec, long j, TimeUnit timeUnit) {
            Preconditions.checkArgument(cacheBuilderSpec.refreshTimeUnit == null, "refreshAfterWrite already set");
            cacheBuilderSpec.refreshDuration = j;
            cacheBuilderSpec.refreshTimeUnit = timeUnit;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface ValueParser {
        void parse(CacheBuilderSpec cacheBuilderSpec, String str, @Nullable String str2);
    }

    /* loaded from: classes.dex */
    static class ValueStrengthParser implements ValueParser {
        private final LocalCache.Strength strength;

        public ValueStrengthParser(LocalCache.Strength strength) {
            this.strength = strength;
        }

        @Override // com.google.common.cache.CacheBuilderSpec.ValueParser
        public void parse(CacheBuilderSpec cacheBuilderSpec, String str, @Nullable String str2) {
            Preconditions.checkArgument(str2 == null, "key %s does not take values", str);
            Preconditions.checkArgument(cacheBuilderSpec.valueStrength == null, "%s was already set to %s", str, cacheBuilderSpec.valueStrength);
            cacheBuilderSpec.valueStrength = this.strength;
        }
    }

    /* loaded from: classes.dex */
    static class WriteDurationParser extends DurationParser {
        WriteDurationParser() {
        }

        @Override // com.google.common.cache.CacheBuilderSpec.DurationParser
        protected void parseDuration(CacheBuilderSpec cacheBuilderSpec, long j, TimeUnit timeUnit) {
            Preconditions.checkArgument(cacheBuilderSpec.writeExpirationTimeUnit == null, "expireAfterWrite already set");
            cacheBuilderSpec.writeExpirationDuration = j;
            cacheBuilderSpec.writeExpirationTimeUnit = timeUnit;
        }
    }

    private CacheBuilderSpec(String str) {
        this.specification = str;
    }

    public static CacheBuilderSpec disableCaching() {
        return parse("maximumSize=0");
    }

    @Nullable
    private static Long durationInNanos(long j, @Nullable TimeUnit timeUnit) {
        if (timeUnit != null) {
            return Long.valueOf(timeUnit.toNanos(j));
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String format(String str, Object... objArr) {
        return String.format(Locale.ROOT, str, objArr);
    }

    public static CacheBuilderSpec parse(String str) {
        CacheBuilderSpec cacheBuilderSpec = new CacheBuilderSpec(str);
        if (!str.isEmpty()) {
            for (String str2 : KEYS_SPLITTER.split(str)) {
                ImmutableList copyOf = ImmutableList.copyOf(KEY_VALUE_SPLITTER.split(str2));
                Preconditions.checkArgument(!copyOf.isEmpty(), "blank key-value pair");
                Preconditions.checkArgument(copyOf.size() <= 2, "key-value pair %s with more than one equals sign", str2);
                String str3 = (String) copyOf.get(0);
                ValueParser valueParser = VALUE_PARSERS.get(str3);
                Preconditions.checkArgument(valueParser != null, "unknown key %s", str3);
                valueParser.parse(cacheBuilderSpec, str3, copyOf.size() != 1 ? (String) copyOf.get(1) : null);
            }
        }
        return cacheBuilderSpec;
    }

    public boolean equals(@Nullable Object obj) {
        if (this != obj) {
            if (obj instanceof CacheBuilderSpec) {
                CacheBuilderSpec cacheBuilderSpec = (CacheBuilderSpec) obj;
                return Objects.equal(this.initialCapacity, cacheBuilderSpec.initialCapacity) && Objects.equal(this.maximumSize, cacheBuilderSpec.maximumSize) && Objects.equal(this.maximumWeight, cacheBuilderSpec.maximumWeight) && Objects.equal(this.concurrencyLevel, cacheBuilderSpec.concurrencyLevel) && Objects.equal(this.keyStrength, cacheBuilderSpec.keyStrength) && Objects.equal(this.valueStrength, cacheBuilderSpec.valueStrength) && Objects.equal(this.recordStats, cacheBuilderSpec.recordStats) && Objects.equal(durationInNanos(this.writeExpirationDuration, this.writeExpirationTimeUnit), durationInNanos(cacheBuilderSpec.writeExpirationDuration, cacheBuilderSpec.writeExpirationTimeUnit)) && Objects.equal(durationInNanos(this.accessExpirationDuration, this.accessExpirationTimeUnit), durationInNanos(cacheBuilderSpec.accessExpirationDuration, cacheBuilderSpec.accessExpirationTimeUnit)) && Objects.equal(durationInNanos(this.refreshDuration, this.refreshTimeUnit), durationInNanos(cacheBuilderSpec.refreshDuration, cacheBuilderSpec.refreshTimeUnit));
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Objects.hashCode(this.initialCapacity, this.maximumSize, this.maximumWeight, this.concurrencyLevel, this.keyStrength, this.valueStrength, this.recordStats, durationInNanos(this.writeExpirationDuration, this.writeExpirationTimeUnit), durationInNanos(this.accessExpirationDuration, this.accessExpirationTimeUnit), durationInNanos(this.refreshDuration, this.refreshTimeUnit));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CacheBuilder<Object, Object> toCacheBuilder() {
        CacheBuilder<Object, Object> newBuilder = CacheBuilder.newBuilder();
        if (this.initialCapacity != null) {
            newBuilder.initialCapacity(this.initialCapacity.intValue());
        }
        if (this.maximumSize != null) {
            newBuilder.maximumSize(this.maximumSize.longValue());
        }
        if (this.maximumWeight != null) {
            newBuilder.maximumWeight(this.maximumWeight.longValue());
        }
        if (this.concurrencyLevel != null) {
            newBuilder.concurrencyLevel(this.concurrencyLevel.intValue());
        }
        if (this.keyStrength != null) {
            switch (this.keyStrength) {
                case WEAK:
                    newBuilder.weakKeys();
                    break;
                default:
                    throw new AssertionError();
            }
        }
        if (this.valueStrength != null) {
            switch (this.valueStrength) {
                case WEAK:
                    newBuilder.weakValues();
                    break;
                case SOFT:
                    newBuilder.softValues();
                    break;
                default:
                    throw new AssertionError();
            }
        }
        if (this.recordStats != null && this.recordStats.booleanValue()) {
            newBuilder.recordStats();
        }
        if (this.writeExpirationTimeUnit != null) {
            newBuilder.expireAfterWrite(this.writeExpirationDuration, this.writeExpirationTimeUnit);
        }
        if (this.accessExpirationTimeUnit != null) {
            newBuilder.expireAfterAccess(this.accessExpirationDuration, this.accessExpirationTimeUnit);
        }
        if (this.refreshTimeUnit != null) {
            newBuilder.refreshAfterWrite(this.refreshDuration, this.refreshTimeUnit);
        }
        return newBuilder;
    }

    public String toParsableString() {
        return this.specification;
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).addValue(toParsableString()).toString();
    }
}
