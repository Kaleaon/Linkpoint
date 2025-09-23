#@category Lumiya
# Dumps a JSON list of functions (name, entry, signature, calling convention) for the current program.
# Usage (headless): -postScript dump_symbols.py /path/to/output.json
import json

def main():
    args = getScriptArgs()
    if not args or len(args) < 1:
        print("Usage: dump_symbols.py /path/to/output.json")
        return

    out_path = args[0]
    fm = currentProgram.getFunctionManager()
    funcs = []
    it = fm.getFunctions(True)
    for f in it:
        try:
            funcs.append({
                "name": f.getName(),
                "entry": str(f.getEntryPoint()),
                "signature": str(f.getSignature()),
                "calling_convention": f.getCallingConventionName() or ""
            })
        except Exception as e:
            print("Error on function:", e)

    meta = {
        "program_name": currentProgram.getName(),
        "image_base": str(currentProgram.getImageBase()),
        "language": str(currentProgram.getLanguageID()),
        "compiler": str(currentProgram.getCompilerSpec().getCompilerSpecID())
    }

    with open(out_path, "w") as fp:
        json.dump({"meta": meta, "functions": funcs}, fp, indent=2)

if __name__ == "__main__":
    main()