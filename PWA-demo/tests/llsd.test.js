const LLSD = require('../js/llsd.js');

// Mock DOMParser for Node.js environment
if (typeof DOMParser === 'undefined') {
  global.DOMParser = class {
    parseFromString(str, type) {
      const _parseChildren = (xml) => {
          // Very basic regex parser for testing purposes
          const matches = [];
          // Match simple tags: <tag>content</tag>
          const regex = /<(\w+)>(.*?)<\/\1>/g;
          let match;
          while ((match = regex.exec(xml)) !== null) {
            matches.push({
              nodeType: 1,
              tagName: match[1],
              textContent: match[2],
              children: [] // No nested support in this simple mock
            });
          }
          // Special case for empty tag <undef />
          if (xml.includes('<undef />')) {
            matches.push({ nodeType: 1, tagName: 'undef', textContent: '' });
          }
          return matches;
      };

      return {
        querySelector: (selector) => {
          if (selector === 'parsererror') return null;
          if (selector === 'llsd') {
            // Strip outer <llsd> tag if present to get children
            // This is a simplified mock behavior
            const innerContent = str.replace(/<\/?llsd>/g, '').trim();
            return {
              children: _parseChildren(innerContent)
            };
          }
          return null;
        }
      };
    }
  };
}

// Tests
console.log('Running LLSD Tests...');

let failures = 0;

function assert(condition, message) {
  if (!condition) {
    console.error(`❌ FAIL: ${message}`);
    failures++;
  } else {
    console.log(`✅ PASS: ${message}`);
  }
}

function assertEqual(actual, expected, message) {
  const actualStr = JSON.stringify(actual);
  const expectedStr = JSON.stringify(expected);
  if (actualStr !== expectedStr) {
    console.error(`❌ FAIL: ${message}`);
    console.error(`   Expected: ${expectedStr}`);
    console.error(`   Actual:   ${actualStr}`);
    failures++;
  } else {
    console.log(`✅ PASS: ${message}`);
  }
}

// Test buildXML
try {
  const data = {
    key1: 'value1',
    key2: 123,
    key3: true,
    key4: [1, 2, 3]
  };

  const xml = LLSD.buildXML(data);
  console.log('Generated XML:', xml);

  assert(xml.includes('<map>'), 'XML should contain map');
  assert(xml.includes('<key>key1</key><string>value1</string>'), 'XML should contain key1 string');
  assert(xml.includes('<key>key2</key><integer>123</integer>'), 'XML should contain key2 integer');
  // We updated code to use true/false
  assert(xml.includes('<key>key3</key><boolean>true</boolean>'), 'XML should contain key3 boolean');
  assert(xml.includes('<array><integer>1</integer><integer>2</integer><integer>3</integer></array>'), 'XML should contain array');

} catch (e) {
  console.error('❌ FAIL: buildXML threw exception', e);
  failures++;
}

// Test parseXML (using mock)
try {
  // Simple XML test that matches our mock's capabilities
  const simpleXml = '<llsd><string>test value</string></llsd>';
  const result = LLSD.parseXML(simpleXml);
  assertEqual(result, 'test value', 'parseXML should parse string');

  const intXml = '<llsd><integer>42</integer></llsd>';
  const intResult = LLSD.parseXML(intXml);
  assertEqual(intResult, 42, 'parseXML should parse integer');

} catch (e) {
  console.error('❌ FAIL: parseXML threw exception', e);
  failures++;
}

if (failures === 0) {
  console.log('All tests passed!');
} else {
  console.error(`${failures} tests failed.`);
  process.exit(1);
}
