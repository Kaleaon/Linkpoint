#!/usr/bin/env python3
"""
Advanced Java to Kotlin Converter
Specifically tuned for Android and Second Life protocol code
Handles complex patterns, annotations, and Android-specific constructs
"""

import os
import sys
import re
from pathlib import Path
from typing import Optional, List, Tuple, Dict
import subprocess

class AdvancedJavaToKotlinConverter:
    """Advanced converter with Android and SL protocol support"""
    
    def __init__(self):
        self.conversions = []
        self.errors = []
        
    def convert_file(self, java_file: str, kotlin_file: str, backup_dir: str = None) -> bool:
        """Convert a Java file to Kotlin with validation"""
        try:
            # Read Java source
            with open(java_file, 'r', encoding='utf-8', errors='ignore') as f:
                java_code = f.read()
            
            # Detect file type and apply appropriate conversion
            file_type = self.detect_file_type(java_code)
            print(f"Converting {os.path.basename(java_file)} (type: {file_type})")
            
            # Perform conversion based on type
            kotlin_code = self.convert_by_type(java_code, file_type)
            
            # Ensure newline at end
            if not kotlin_code.endswith('\n'):
                kotlin_code += '\n'
            
            # Write Kotlin output
            os.makedirs(os.path.dirname(kotlin_file), exist_ok=True)
            with open(kotlin_file, 'w', encoding='utf-8') as f:
                f.write(kotlin_code)
            
            print(f"✓ Converted: {java_file} -> {kotlin_file}")
            return True
            
        except Exception as e:
            print(f"✗ Error converting {java_file}: {e}", file=sys.stderr)
            self.errors.append((java_file, str(e)))
            return False
    
    def detect_file_type(self, code: str) -> str:
        """Detect the type of Java file"""
        if re.search(r'\binterface\s+\w+', code):
            return 'interface'
        elif re.search(r'\benum\s+\w+', code):
            return 'enum'
        elif re.search(r'\babstract\s+class', code):
            return 'abstract_class'
        elif re.search(r'\bclass\s+\w+\s+extends\s+Exception', code):
            return 'exception'
        elif re.search(r'@Entity|@Table|@Column', code):
            return 'entity'
        elif self.is_data_class(code):
            return 'data_class'
        elif self.is_utility_class(code):
            return 'utility'
        elif re.search(r'extends\s+(Activity|Fragment|Service|BroadcastReceiver)', code):
            return 'android_component'
        else:
            return 'class'
    
    def is_data_class(self, code: str) -> bool:
        """Check if this is a POJO/data class"""
        has_fields = bool(re.search(r'private\s+\w+\s+\w+;', code))
        has_getters = bool(re.search(r'public\s+\w+\s+get\w+\(\)', code))
        has_setters = bool(re.search(r'public\s+void\s+set\w+\(', code))
        has_equals = bool(re.search(r'public\s+boolean\s+equals\(', code))
        
        return has_fields and (has_getters or has_setters or has_equals)
    
    def is_utility_class(self, code: str) -> bool:
        """Check if this is a utility class with static methods"""
        has_static_methods = bool(re.search(r'public\s+static\s+\w+\s+\w+\(', code))
        has_private_constructor = bool(re.search(r'private\s+\w+\s*\(\s*\)', code))
        
        return has_static_methods and (has_private_constructor or not re.search(r'public\s+\w+\s*\(', code))
    
    def convert_by_type(self, code: str, file_type: str) -> str:
        """Convert code based on detected type"""
        if file_type == 'interface':
            return self.convert_interface(code)
        elif file_type == 'enum':
            return self.convert_enum(code)
        elif file_type == 'exception':
            return self.convert_exception(code)
        elif file_type == 'data_class':
            return self.convert_data_class(code)
        elif file_type == 'utility':
            return self.convert_utility_class(code)
        elif file_type == 'abstract_class':
            return self.convert_abstract_class(code)
        elif file_type == 'android_component':
            return self.convert_android_component(code)
        else:
            return self.convert_regular_class(code)
    
    def convert_interface(self, code: str) -> str:
        """Convert Java interface to Kotlin"""
        kotlin = code
        
        # Remove public modifier
        kotlin = re.sub(r'\bpublic\s+interface\b', 'interface', kotlin)
        
        # Convert method signatures
        kotlin = re.sub(
            r'(\w+(?:<[^>]+>)?)\s+(\w+)\s*\(([^)]*)\);',
            lambda m: f'fun {m.group(2)}({self.convert_parameters(m.group(3))}): {self.convert_type(m.group(1))}',
            kotlin
        )
        
        # Convert void methods
        kotlin = re.sub(
            r'void\s+(\w+)\s*\(([^)]*)\);',
            lambda m: f'fun {m.group(1)}({self.convert_parameters(m.group(2))})',
            kotlin
        )
        
        # Remove semicolons
        kotlin = self.remove_semicolons(kotlin)
        
        # Clean up
        kotlin = self.clean_syntax(kotlin)
        
        return kotlin
    
    def convert_enum(self, code: str) -> str:
        """Convert Java enum to Kotlin enum class"""
        kotlin = code
        
        # Convert enum declaration
        kotlin = re.sub(r'\bpublic\s+enum\b', 'enum class', kotlin)
        kotlin = re.sub(r'\benum\b', 'enum class', kotlin)
        
        # Move static constants to companion object
        static_constants = re.findall(
            r'public\s+static\s+final\s+(\w+)\s+(\w+)\s*=\s*([^;]+);',
            kotlin
        )
        
        if static_constants:
            # Remove static constants from main body
            for const in static_constants:
                kotlin = re.sub(
                    rf'public\s+static\s+final\s+{const[0]}\s+{const[1]}\s*=\s*{re.escape(const[2])};',
                    '',
                    kotlin
                )
            
            # Add companion object
            companion = '\n    companion object {\n'
            for const in static_constants:
                companion += f'        const val {const[1]} = {const[2]}\n'
            companion += '    }\n'
            
            # Insert before closing brace
            kotlin = re.sub(r'(\n\s*})$', companion + r'\1', kotlin)
        
        # Remove semicolons
        kotlin = self.remove_semicolons(kotlin)
        
        # Clean up
        kotlin = self.clean_syntax(kotlin)
        
        return kotlin
    
    def convert_exception(self, code: str) -> str:
        """Convert Java exception class to Kotlin"""
        kotlin = code
        
        # Extract class name and parent
        match = re.search(r'class\s+(\w+)\s+extends\s+(\w+)', kotlin)
        if match:
            class_name = match.group(1)
            parent_class = match.group(2)
            
            # Find constructor
            constructor_match = re.search(
                rf'public\s+{class_name}\s*\(([^)]*)\)\s*{{\s*super\(([^)]*)\);?\s*}}',
                kotlin
            )
            
            if constructor_match:
                params = constructor_match.group(1)
                super_params = constructor_match.group(2)
                
                # Create Kotlin version
                kotlin_params = self.convert_parameters(params)
                kotlin = f'''package {self.extract_package(code)}

class {class_name}({kotlin_params}) : {parent_class}({super_params})
'''
        
        return kotlin
    
    def convert_data_class(self, code: str) -> str:
        """Convert POJO to Kotlin data class"""
        kotlin = code
        
        # Extract class name
        class_match = re.search(r'class\s+(\w+)', kotlin)
        if not class_match:
            return self.convert_regular_class(code)
        
        class_name = class_match.group(1)
        
        # Extract fields
        fields = re.findall(
            r'private\s+(\w+(?:<[^>]+>)?)\s+(\w+)(?:\s*=\s*([^;]+))?;',
            kotlin
        )
        
        if not fields:
            return self.convert_regular_class(code)
        
        # Build data class
        package = self.extract_package(code)
        imports = self.extract_imports(code)
        
        result = f'package {package}\n\n'
        if imports:
            result += '\n'.join(imports) + '\n\n'
        
        result += f'data class {class_name}(\n'
        
        for i, (field_type, field_name, default_value) in enumerate(fields):
            kotlin_type = self.convert_type(field_type)
            
            # Determine if var or val (use var for mutable)
            result += f'    var {field_name}: {kotlin_type}'
            
            if default_value:
                result += f' = {default_value}'
            elif self.is_primitive_type(kotlin_type):
                result += f' = {self.get_default_value(kotlin_type)}'
            
            if i < len(fields) - 1:
                result += ','
            result += '\n'
        
        result += ')'
        
        # Add companion object if there are static constants
        static_constants = re.findall(
            r'public\s+static\s+final\s+(\w+)\s+(\w+)\s*=\s*([^;]+);',
            kotlin
        )
        
        if static_constants:
            result += ' {\n    companion object {\n'
            for const_type, const_name, const_value in static_constants:
                result += f'        const val {const_name} = {const_value}\n'
            result += '    }\n}'
        
        result += '\n'
        
        return result
    
    def convert_utility_class(self, code: str) -> str:
        """Convert utility class to Kotlin object"""
        kotlin = code
        
        # Convert class to object
        kotlin = re.sub(r'\bclass\s+(\w+)', r'object \1', kotlin)
        
        # Remove private constructor
        kotlin = re.sub(r'private\s+\w+\s*\(\s*\)\s*\{\s*\}', '', kotlin)
        
        # Convert static methods
        kotlin = self.convert_methods(kotlin)
        
        # Remove static keyword
        kotlin = re.sub(r'\bstatic\s+', '', kotlin)
        
        # Remove semicolons
        kotlin = self.remove_semicolons(kotlin)
        
        # Clean up
        kotlin = self.clean_syntax(kotlin)
        
        return kotlin
    
    def convert_abstract_class(self, code: str) -> str:
        """Convert abstract class"""
        kotlin = code
        
        # Keep abstract keyword
        kotlin = re.sub(r'\bpublic\s+abstract\s+class\b', 'abstract class', kotlin)
        
        # Convert methods
        kotlin = self.convert_methods(kotlin)
        
        # Convert fields
        kotlin = self.convert_fields(kotlin)
        
        # Convert constructors
        kotlin = self.convert_constructors(kotlin)
        
        # Handle abstract methods
        kotlin = re.sub(
            r'abstract\s+(\w+(?:<[^>]+>)?)\s+(\w+)\s*\(([^)]*)\);',
            lambda m: f'abstract fun {m.group(2)}({self.convert_parameters(m.group(3))}): {self.convert_type(m.group(1))}',
            kotlin
        )
        
        # Remove semicolons
        kotlin = self.remove_semicolons(kotlin)
        
        # Clean up
        kotlin = self.clean_syntax(kotlin)
        
        return kotlin
    
    def convert_android_component(self, code: str) -> str:
        """Convert Android component (Activity, Fragment, etc.)"""
        kotlin = code
        
        # Convert extends to :
        kotlin = re.sub(r'\bextends\b', ':', kotlin)
        
        # Convert onCreate and lifecycle methods
        kotlin = re.sub(
            r'@Override\s+protected\s+void\s+onCreate\s*\(Bundle\s+savedInstanceState\)',
            'override fun onCreate(savedInstanceState: Bundle?)',
            kotlin
        )
        
        kotlin = re.sub(
            r'@Override\s+protected\s+void\s+onResume\s*\(\s*\)',
            'override fun onResume()',
            kotlin
        )
        
        # Convert findViewById
        kotlin = re.sub(
            r'findViewById\s*\(R\.id\.(\w+)\)',
            r'findViewById<View>(R.id.\1)',
            kotlin
        )
        
        # Convert methods
        kotlin = self.convert_methods(kotlin)
        
        # Convert fields
        kotlin = self.convert_fields(kotlin)
        
        # Remove semicolons
        kotlin = self.remove_semicolons(kotlin)
        
        # Clean up
        kotlin = self.clean_syntax(kotlin)
        
        return kotlin
    
    def convert_regular_class(self, code: str) -> str:
        """Convert regular class"""
        kotlin = code
        
        # Apply all conversions
        kotlin = self.convert_package_and_imports(kotlin)
        kotlin = self.convert_class_declaration(kotlin)
        kotlin = self.convert_fields(kotlin)
        kotlin = self.convert_constructors(kotlin)
        kotlin = self.convert_methods(kotlin)
        kotlin = self.convert_types(kotlin)
        kotlin = self.convert_arrays(kotlin)
        kotlin = self.convert_new_keyword(kotlin)
        kotlin = self.convert_null_checks(kotlin)
        kotlin = self.convert_string_operations(kotlin)
        kotlin = self.convert_collections(kotlin)
        kotlin = self.remove_semicolons(kotlin)
        kotlin = self.clean_syntax(kotlin)
        
        return kotlin
    
    def convert_package_and_imports(self, code: str) -> str:
        """Convert package and imports"""
        code = re.sub(r'package\s+([^;]+);', r'package \1', code)
        code = re.sub(r'import\s+([^;]+);', r'import \1', code)
        return code
    
    def convert_class_declaration(self, code: str) -> str:
        """Convert class declarations"""
        code = re.sub(r'\bpublic\s+class\b', 'class', code)
        code = re.sub(r'\bpublic\s+interface\b', 'interface', code)
        code = re.sub(r'\bextends\b', ':', code)
        code = re.sub(r'\bimplements\b', ':', code)
        return code
    
    def convert_fields(self, code: str) -> str:
        """Convert field declarations"""
        # Convert final fields to val
        code = re.sub(
            r'private\s+final\s+(\w+(?:<[^>]+>)?)\s+(\w+)\s*=\s*([^;]+);',
            lambda m: f'private val {m.group(2)}: {self.convert_type(m.group(1))} = {m.group(3)}',
            code
        )
        
        # Convert mutable fields to var
        code = re.sub(
            r'private\s+(\w+(?:<[^>]+>)?)\s+(\w+)\s*=\s*([^;]+);',
            lambda m: f'private var {m.group(2)}: {self.convert_type(m.group(1))} = {m.group(3)}',
            code
        )
        
        # Convert fields without initialization
        code = re.sub(
            r'private\s+(\w+(?:<[^>]+>)?)\s+(\w+);',
            lambda m: f'private var {m.group(2)}: {self.convert_type(m.group(1))}? = null',
            code
        )
        
        # Handle @Volatile annotation
        code = re.sub(r'@Volatile', '@Volatile', code)
        
        return code
    
    def convert_constructors(self, code: str) -> str:
        """Convert constructors"""
        # Find class name
        class_match = re.search(r'class\s+(\w+)', code)
        if not class_match:
            return code
        
        class_name = class_match.group(1)
        
        # Convert constructors
        code = re.sub(
            rf'public\s+{class_name}\s*\(([^)]*)\)\s*{{',
            lambda m: f'constructor({self.convert_parameters(m.group(1))}) {{',
            code
        )
        
        return code
    
    def convert_methods(self, code: str) -> str:
        """Convert method declarations"""
        # Convert @Override to override
        code = re.sub(r'@Override\s+', 'override ', code)
        
        # Convert public methods with return type
        code = re.sub(
            r'public\s+(\w+(?:<[^>]+>)?)\s+(\w+)\s*\(([^)]*)\)\s*{',
            lambda m: f'fun {m.group(2)}({self.convert_parameters(m.group(3))}): {self.convert_type(m.group(1))} {{',
            code
        )
        
        # Convert public void methods
        code = re.sub(
            r'public\s+void\s+(\w+)\s*\(([^)]*)\)\s*{',
            lambda m: f'fun {m.group(1)}({self.convert_parameters(m.group(2))}) {{',
            code
        )
        
        # Convert protected methods
        code = re.sub(
            r'protected\s+(\w+(?:<[^>]+>)?)\s+(\w+)\s*\(([^)]*)\)\s*{',
            lambda m: f'protected fun {m.group(2)}({self.convert_parameters(m.group(3))}): {self.convert_type(m.group(1))} {{',
            code
        )
        
        # Convert private methods
        code = re.sub(
            r'private\s+(\w+(?:<[^>]+>)?)\s+(\w+)\s*\(([^)]*)\)\s*{',
            lambda m: f'private fun {m.group(2)}({self.convert_parameters(m.group(3))}): {self.convert_type(m.group(1))} {{',
            code
        )
        
        # Convert static methods
        code = re.sub(
            r'public\s+static\s+(\w+(?:<[^>]+>)?)\s+(\w+)\s*\(([^)]*)\)\s*{',
            lambda m: f'fun {m.group(2)}({self.convert_parameters(m.group(3))}): {self.convert_type(m.group(1))} {{',
            code
        )
        
        return code
    
    def convert_parameters(self, params: str) -> str:
        """Convert method parameters"""
        if not params.strip():
            return ''
        
        param_list = [p.strip() for p in params.split(',')]
        converted = []
        
        for param in param_list:
            if not param:
                continue
            
            # Handle annotations
            param = re.sub(r'@\w+\s+', '', param)
            
            # Handle final
            param = param.replace('final ', '')
            
            parts = param.split()
            if len(parts) >= 2:
                param_type = parts[0]
                param_name = parts[1]
                
                kotlin_type = self.convert_type(param_type)
                converted.append(f'{param_name}: {kotlin_type}')
        
        return ', '.join(converted)
    
    def convert_types(self, code: str) -> str:
        """Convert Java types to Kotlin types"""
        type_map = {
            'String': 'String',
            'int': 'Int',
            'long': 'Long',
            'float': 'Float',
            'double': 'Double',
            'boolean': 'Boolean',
            'byte': 'Byte',
            'short': 'Short',
            'char': 'Char',
            'void': 'Unit',
            'Integer': 'Int',
            'Long': 'Long',
            'Float': 'Float',
            'Double': 'Double',
            'Boolean': 'Boolean',
            'Object': 'Any',
        }
        
        for java_type, kotlin_type in type_map.items():
            code = re.sub(rf'\b{java_type}\b', kotlin_type, code)
        
        return code
    
    def convert_type(self, java_type: str) -> str:
        """Convert a single type name"""
        type_map = {
            'String': 'String',
            'int': 'Int',
            'long': 'Long',
            'float': 'Float',
            'double': 'Double',
            'boolean': 'Boolean',
            'byte': 'Byte',
            'short': 'Short',
            'char': 'Char',
            'void': 'Unit',
            'Integer': 'Int',
            'Object': 'Any',
        }
        
        # Handle array types
        if '[]' in java_type:
            base_type = java_type.replace('[]', '').strip()
            kotlin_base = type_map.get(base_type, base_type)
            if kotlin_base in ['Byte', 'Int', 'Long', 'Float', 'Double', 'Boolean', 'Char', 'Short']:
                return f'{kotlin_base}Array'
            return f'Array<{kotlin_base}>'
        
        return type_map.get(java_type, java_type)
    
    def convert_arrays(self, code: str) -> str:
        """Convert array declarations"""
        code = re.sub(r'\bbyte\[\]', 'ByteArray', code)
        code = re.sub(r'\bint\[\]', 'IntArray', code)
        code = re.sub(r'\blong\[\]', 'LongArray', code)
        code = re.sub(r'\bfloat\[\]', 'FloatArray', code)
        code = re.sub(r'\bdouble\[\]', 'DoubleArray', code)
        code = re.sub(r'\bboolean\[\]', 'BooleanArray', code)
        code = re.sub(r'\bchar\[\]', 'CharArray', code)
        code = re.sub(r'\bString\[\]', 'Array<String>', code)
        
        return code
    
    def convert_new_keyword(self, code: str) -> str:
        """Convert new keyword"""
        code = re.sub(r'\bnew\s+', '', code)
        return code
    
    def convert_null_checks(self, code: str) -> str:
        """Convert null checks to Kotlin style"""
        # Convert: if (obj != null) { obj.method() }
        # To: obj?.method()
        code = re.sub(
            r'if\s*\(\s*(\w+)\s*!=\s*null\s*\)\s*{\s*\1\.(\w+)\(',
            r'\1?.\2(',
            code
        )
        
        return code
    
    def convert_string_operations(self, code: str) -> str:
        """Convert string operations"""
        # Convert .equals() to ==
        code = re.sub(
            r'(\w+)\.equals\(([^)]+)\)',
            r'\1 == \2',
            code
        )
        
        # Convert .length() to .length
        code = re.sub(r'\.length\(\)', '.length', code)
        
        return code
    
    def convert_collections(self, code: str) -> str:
        """Convert collection operations"""
        # Convert ArrayList to mutableListOf
        code = re.sub(
            r'ArrayList<([^>]+)>\(\)',
            r'mutableListOf<\1>()',
            code
        )
        
        # Convert HashMap to mutableMapOf
        code = re.sub(
            r'HashMap<([^>]+)>\(\)',
            r'mutableMapOf<\1>()',
            code
        )
        
        return code
    
    def remove_semicolons(self, code: str) -> str:
        """Remove unnecessary semicolons"""
        lines = code.split('\n')
        result = []
        
        for line in lines:
            # Don't remove semicolons in strings or comments
            if '"' not in line and "'" not in line and '//' not in line:
                line = re.sub(r';(\s*)$', r'\1', line)
            result.append(line)
        
        return '\n'.join(result)
    
    def clean_syntax(self, code: str) -> str:
        """Clean up syntax issues"""
        code = re.sub(r'\bfinal\s+', '', code)
        code = re.sub(r'\bpublic\s+', '', code)
        code = re.sub(r'\bstatic\s+', '', code)
        code = re.sub(r'\bprivate\s+static\s+final\s+long\s+serialVersionUID\s*=\s*[^;]+;', '', code)
        
        return code
    
    def extract_package(self, code: str) -> str:
        """Extract package name"""
        match = re.search(r'package\s+([^;]+)', code)
        return match.group(1) if match else ''
    
    def extract_imports(self, code: str) -> List[str]:
        """Extract import statements"""
        imports = re.findall(r'import\s+([^;]+)', code)
        return [f'import {imp}' for imp in imports]
    
    def is_primitive_type(self, kotlin_type: str) -> bool:
        """Check if type is primitive"""
        primitives = ['Int', 'Long', 'Float', 'Double', 'Boolean', 'Byte', 'Short', 'Char']
        return kotlin_type in primitives
    
    def get_default_value(self, kotlin_type: str) -> str:
        """Get default value for type"""
        defaults = {
            'Int': '0',
            'Long': '0L',
            'Float': '0f',
            'Double': '0.0',
            'Boolean': 'false',
            'Byte': '0',
            'Short': '0',
            'Char': "'\\0'",
        }
        return defaults.get(kotlin_type, 'null')

def main():
    if len(sys.argv) < 3:
        print("Usage: advanced_java2kotlin.py <input.java> <output.kt>")
        sys.exit(1)
    
    java_file = sys.argv[1]
    kotlin_file = sys.argv[2]
    
    if not os.path.exists(java_file):
        print(f"Error: File not found: {java_file}", file=sys.stderr)
        sys.exit(1)
    
    converter = AdvancedJavaToKotlinConverter()
    success = converter.convert_file(java_file, kotlin_file)
    
    sys.exit(0 if success else 1)

if __name__ == '__main__':
    main()