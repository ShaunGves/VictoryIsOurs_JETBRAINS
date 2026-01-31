require.config({ paths: { 'vs': 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.36.1/min/vs' }});

let editor;

require(['vs/editor/editor.main'], function() {
    editor = monaco.editor.create(document.getElementById('editor-container'), {
        value: "public class Main {\n    \n}",
        language: 'java',
        theme: 'vs-dark',
        fontSize: 16,
        automaticLayout: true,
        minimap: { enabled: false },
        scrollBeyondLastLine: false,
        lineNumbers: "on",
        renderLineHighlight: "all"
    });

    // Update language dynamically
    document.getElementById('language-select').addEventListener('change', (e) => {
        const lang = e.target.value;
        monaco.editor.setModelLanguage(editor.getModel(), lang);
        const templates = {
            java: "public class Main {\n    \n}",
            python: "def my_function():\n    print('Hello')",
            javascript: "function test() {\n    console.log('hi');\n}",
            css: ".container {\n    color: red;\n}"
        };
        editor.setValue(templates[lang]);
    });

    // THE STOPPER LOGIC
    editor.onKeyDown((e) => {
        if (e.keyCode === monaco.KeyCode.Enter) {
            const position = editor.getPosition();
            const lineContent = editor.getModel().getLineContent(position.lineNumber);
            const lang = document.getElementById('language-select').value;

            const validation = validateLineStrictly(lineContent, lang);

            if (!validation.valid) {
                e.preventDefault(); // STOP THE KEYBOARD
                e.stopPropagation();
                triggerErrorUI(validation.message);
            } else {
                clearErrorUI();
            }
        }
    });
});

// REFINED VALIDATION ENGINE
function validateLineStrictly(line, lang) {
    const trimmed = line.trim();
    
    // Allow empty lines (user just wants to skip space)
    if (trimmed === "") return { valid: true };

    if (lang === 'java') {
        // 1. Check for missing curly brace on Class/Method definitions
        if ((trimmed.includes("class ") || trimmed.includes("void ") || trimmed.includes("public ")) 
            && !trimmed.endsWith("{") && !trimmed.endsWith(";")) {
            return { valid: false, message: "Java structures must end with '{' or a semicolon ';'" };
        }
        // 2. Check for unbalanced parentheses
        if ((trimmed.split("(").length - 1) !== (trimmed.split(")").length - 1)) {
            return { valid: false, message: "Unbalanced parentheses '()'. Close your arguments." };
        }
        // 3. Check for missing semicolon on standard lines
        if (!trimmed.endsWith("{") && !trimmed.endsWith("}") && !trimmed.endsWith(";") && !trimmed.startsWith("//")) {
            return { valid: false, message: "Missing semicolon ';' at the end of the statement." };
        }
    }

    if (lang === 'python') {
        // 1. Python blocks must end with a colon
        const blockKeywords = ['def', 'if', 'else', 'elif', 'for', 'while', 'class', 'with', 'try', 'except'];
        const firstWord = trimmed.split(' ')[0];
        if (blockKeywords.includes(firstWord) && !trimmed.endsWith(':')) {
            return { valid: false, message: "Python blocks (if, def, etc.) must end with a colon ':'" };
        }
        // 2. Unclosed quotes
        const quotes = (trimmed.match(/'/g) || []).length + (trimmed.match(/"/g) || []).length;
        if (quotes % 2 !== 0) return { valid: false, message: "Unclosed string literal (quote)." };
    }

    if (lang === 'javascript') {
        // Check for unclosed brackets or common errors
        if ((trimmed.includes("function") || trimmed.includes("if") || trimmed.includes("for")) && !trimmed.endsWith("{")) {
            return { valid: false, message: "JS block statement missing opening brace '{'" };
        }
    }

    // Fallback: Check for Monaco's own error markers (useful for CSS/JS)
    const markers = monaco.editor.getModelMarkers({ owner: lang });
    const currentLineMarkers = markers.filter(m => m.startLineNumber === editor.getPosition().lineNumber);
    if (currentLineMarkers.length > 0) {
        return { valid: false, message: currentLineMarkers[0].message };
    }

    return { valid: true };
}

function triggerErrorUI(msg) {
    const container = document.getElementById('editor-container');
    const banner = document.getElementById('error-banner');
    const text = document.getElementById('error-text');
    const light = document.getElementById('status-light');

    container.classList.add('error-border', 'shake');
    banner.classList.remove('hidden');
    text.innerText = msg;
    light.classList.replace('bg-green-500', 'bg-red-500');

    setTimeout(() => container.classList.remove('shake'), 200);
}

function clearErrorUI() {
    const container = document.getElementById('editor-container');
    const banner = document.getElementById('error-banner');
    const light = document.getElementById('status-light');

    container.classList.remove('error-border');
    banner.classList.add('hidden');
    light.classList.replace('bg-red-500', 'bg-green-500');
}