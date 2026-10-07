import { execSync } from 'child_process';
import process from 'process';

const pythonCmd = process.platform === 'win32' ? 'python' : 'python3';
const args = process.argv.slice(2);
const targetArgs = args.length > 0 ? args.join(' ') : '--target typescript,dart,python,c,kotlin,rust,java';

try {
  execSync(`${pythonCmd} tools/protocol_gen/cli.py generate ${targetArgs}`, { stdio: 'inherit' });
} catch (error) {
  process.exit(error.status || 1);
}
