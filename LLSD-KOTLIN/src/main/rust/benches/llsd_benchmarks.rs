/*!
 * LLSD Performance Benchmarks
 * 
 * Copyright (C) 2024 Linden Lab
 */

use criterion::{criterion_group, criterion_main, Criterion, BenchmarkId, Throughput};
use llsd::*;
use std::collections::HashMap;
use uuid::Uuid;

fn create_sample_data(size: usize) -> LLSDValue {
    let mut map = HashMap::new();
    
    for i in 0..size {
        let key = format!("key_{}", i);
        let value = match i % 5 {
            0 => LLSDValue::String(format!("string_value_{}", i)),
            1 => LLSDValue::Integer(i as i32),
            2 => LLSDValue::Real(i as f64 * 3.14159),
            3 => LLSDValue::Boolean(i % 2 == 0),
            4 => LLSDValue::UUID(Uuid::new_v4()),
            _ => unreachable!(),
        };
        map.insert(key, value);
    }
    
    LLSDValue::Map(map)
}

fn bench_json_parsing(c: &mut Criterion) {
    let mut group = c.benchmark_group("json_parsing");
    
    for size in [100, 1000, 10000].iter() {
        let data = create_sample_data(*size);
        let doc = LLSDDocument::new(data);
        let json_string = LLSDFactory::serialize_json(&doc, false).unwrap();
        
        group.throughput(Throughput::Bytes(json_string.len() as u64));
        group.bench_with_input(
            BenchmarkId::new("parse", size),
            &json_string,
            |b, json| {
                b.iter(|| LLSDFactory::parse_json(json).unwrap());
            }
        );
        
        group.bench_with_input(
            BenchmarkId::new("serialize", size),
            &doc,
            |b, document| {
                b.iter(|| LLSDFactory::serialize_json(document, false).unwrap());
            }
        );
    }
    
    group.finish();
}

fn bench_xml_parsing(c: &mut Criterion) {
    let mut group = c.benchmark_group("xml_parsing");
    
    for size in [100, 1000, 5000].iter() {
        let data = create_sample_data(*size);
        let doc = LLSDDocument::new(data);
        let xml_string = LLSDFactory::serialize_xml(&doc, false).unwrap();
        
        group.throughput(Throughput::Bytes(xml_string.len() as u64));
        group.bench_with_input(
            BenchmarkId::new("parse", size),
            &xml_string,
            |b, xml| {
                b.iter(|| LLSDFactory::parse_xml(xml).unwrap());
            }
        );
        
        group.bench_with_input(
            BenchmarkId::new("serialize", size),
            &doc,
            |b, document| {
                b.iter(|| LLSDFactory::serialize_xml(document, false).unwrap());
            }
        );
    }
    
    group.finish();
}

fn bench_binary_parsing(c: &mut Criterion) {
    let mut group = c.benchmark_group("binary_parsing");
    
    for size in [100, 1000, 10000].iter() {
        let data = create_sample_data(*size);
        let doc = LLSDDocument::new(data);
        let binary_data = LLSDFactory::serialize_binary(&doc).unwrap();
        
        group.throughput(Throughput::Bytes(binary_data.len() as u64));
        group.bench_with_input(
            BenchmarkId::new("parse", size),
            &binary_data,
            |b, binary| {
                b.iter(|| LLSDFactory::parse_binary(binary).unwrap());
            }
        );
        
        group.bench_with_input(
            BenchmarkId::new("serialize", size),
            &doc,
            |b, document| {
                b.iter(|| LLSDFactory::serialize_binary(document).unwrap());
            }
        );
    }
    
    group.finish();
}

fn bench_deep_cloning(c: &mut Criterion) {
    let mut group = c.benchmark_group("deep_cloning");
    
    for size in [100, 1000, 10000].iter() {
        let data = create_sample_data(*size);
        
        group.throughput(Throughput::Elements(*size as u64));
        group.bench_with_input(
            BenchmarkId::new("clone", size),
            &data,
            |b, data| {
                b.iter(|| LLSDUtils::deep_clone(data));
            }
        );
    }
    
    group.finish();
}

fn bench_path_navigation(c: &mut Criterion) {
    let mut group = c.benchmark_group("path_navigation");
    
    // Create nested structure for path testing
    let nested_data = LLSDValue::Map({
        let mut root = HashMap::new();
        for i in 0..100 {
            let level1_key = format!("level1_{}", i);
            let level1_map = {
                let mut l1 = HashMap::new();
                for j in 0..10 {
                    let level2_key = format!("level2_{}", j);
                    l1.insert(level2_key, LLSDValue::String(format!("value_{}_{}", i, j)));
                }
                l1
            };
            root.insert(level1_key, LLSDValue::Map(level1_map));
        }
        root
    });
    
    group.bench_function("get_path", |b| {
        b.iter(|| {
            nested_data.get_path("level1_50.level2_5").unwrap();
        });
    });
    
    group.bench_function("utils_get_string", |b| {
        b.iter(|| {
            LLSDUtils::get_string(&nested_data, "level1_50.level2_5", "default");
        });
    });
    
    group.finish();
}

fn bench_validation(c: &mut Criterion) {
    let mut group = c.benchmark_group("validation");
    
    let test_data = create_sample_data(1000);
    
    group.bench_function("structure_constraints", |b| {
        b.iter(|| {
            LLSDUtils::validate_constraints(&test_data, 10, 2000).unwrap();
        });
    });
    
    group.bench_function("count_elements", |b| {
        b.iter(|| {
            LLSDUtils::count_elements(&test_data);
        });
    });
    
    group.bench_function("max_depth", |b| {
        b.iter(|| {
            LLSDUtils::max_depth(&test_data);
        });
    });
    
    group.finish();
}

#[cfg(feature = "firestorm")]
fn bench_cache_operations(c: &mut Criterion) {
    use llsd::firestorm::FSLLSDCache;
    
    let mut group = c.benchmark_group("cache_operations");
    let cache = FSLLSDCache::new(60000); // 1 minute TTL
    let test_data = create_sample_data(100);
    
    group.bench_function("cache_put", |b| {
        let mut counter = 0;
        b.iter(|| {
            let key = format!("key_{}", counter);
            cache.put(&key, test_data.clone());
            counter += 1;
        });
    });
    
    // Pre-populate cache for get benchmark
    for i in 0..1000 {
        cache.put(&format!("test_key_{}", i), test_data.clone());
    }
    
    group.bench_function("cache_get", |b| {
        let mut counter = 0;
        b.iter(|| {
            let key = format!("test_key_{}", counter % 1000);
            cache.get(&key);
            counter += 1;
        });
    });
    
    group.finish();
}

#[cfg(not(feature = "firestorm"))]
fn bench_cache_operations(_c: &mut Criterion) {
    // Cache benchmarks only available with firestorm feature
}

criterion_group!(
    benches,
    bench_json_parsing,
    bench_xml_parsing,
    bench_binary_parsing,
    bench_deep_cloning,
    bench_path_navigation,
    bench_validation,
    bench_cache_operations
);

criterion_main!(benches);